package com.github.mckernant1.minecraft.jocky.ecs

import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ecs.EcsClient

internal val ecsClient by lazy {
    EcsClient.builder()
        .build()
}

internal val ec2Client by lazy {
    Ec2Client.builder()
        .build()
}
