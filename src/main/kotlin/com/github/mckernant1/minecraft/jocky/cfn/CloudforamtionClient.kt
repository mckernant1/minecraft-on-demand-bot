package com.github.mckernant1.minecraft.jocky.cfn

import software.amazon.awssdk.services.cloudformation.CloudFormationClient

internal val cfnClient = CloudFormationClient.builder()
    .build()
