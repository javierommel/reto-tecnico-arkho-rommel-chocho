#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { FleetStack } from '../lib/fleet-stack';

const app = new cdk.App();

new FleetStack(app, 'FleetStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});