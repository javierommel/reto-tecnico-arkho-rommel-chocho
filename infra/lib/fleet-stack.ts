import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as ecsPatterns from 'aws-cdk-lib/aws-ecs-patterns';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as sqs from 'aws-cdk-lib/aws-sqs';

export class FleetStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, 'FleetVpc', {
      maxAzs: 2,
      natGateways: 1,
    });


    const dbCluster = new rds.DatabaseCluster(this, 'FleetDatabase', {
      engine: rds.DatabaseClusterEngine.auroraPostgres({
        version: rds.AuroraPostgresEngineVersion.VER_15_8,
      }),
      credentials: rds.Credentials.fromGeneratedSecret('postgres'),
      instances: 1,
      instanceProps: {
        vpc,
        instanceType: ec2.InstanceType.of(
          ec2.InstanceClass.T3,
          ec2.InstanceSize.MEDIUM
        ),
      },
      defaultDatabaseName: 'fleetdb',
    });


    const documentsBucket = new s3.Bucket(this, 'DocumentsBucket', {
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
      removalPolicy: cdk.RemovalPolicy.DESTROY, // solo para entorno dev
      autoDeleteObjects: true,
    });


    const solicitudQueue = new sqs.Queue(this, 'SolicitudQueue', {
      visibilityTimeout: cdk.Duration.seconds(30),
    });


    const cluster = new ecs.Cluster(this, 'FleetCluster', {
      vpc,
    });

    const fargateService =
      new ecsPatterns.ApplicationLoadBalancedFargateService(
        this,
        'FleetService',
        {
          cluster,
          cpu: 512,
          memoryLimitMiB: 1024,
          minHealthyPercent: 100,
          maxHealthyPercent: 200,

          desiredCount: 1,
          publicLoadBalancer: true,
          taskImageOptions: {
            image: ecs.ContainerImage.fromRegistry(
              'amazon/amazon-ecs-sample'
            ),
            containerPort: 8080,
            environment: {
              SPRING_DATASOURCE_URL: `jdbc:postgresql://${dbCluster.clusterEndpoint.hostname}:5432/fleetdb`,
              SPRING_DATASOURCE_USERNAME: 'postgres',
              S3_BUCKET_NAME: documentsBucket.bucketName,
              SQS_QUEUE_URL: solicitudQueue.queueUrl,
            },
            secrets: {
              SPRING_DATASOURCE_PASSWORD: ecs.Secret.fromSecretsManager(
                dbCluster.secret!,
                'password'
              ),
            },
          },
        }
      );


    // Acceso a la base de datos
    dbCluster.connections.allowDefaultPortFrom(
      fargateService.service
    );

    // Acceso a S3
    documentsBucket.grantReadWrite(
      fargateService.taskDefinition.taskRole
    );

    // Enviar mensajes a SQS
    solicitudQueue.grantSendMessages(
      fargateService.taskDefinition.taskRole
    );
  }
}
