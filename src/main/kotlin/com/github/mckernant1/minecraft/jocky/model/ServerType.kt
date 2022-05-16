package com.github.mckernant1.minecraft.jocky.model

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnumAttributeConverter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

enum class ServerType {
    VANILLA,
    CURSEFORGE;
}


class ServerTypeAttributeConverter : AttributeConverter<ServerType> {
    private val converter = EnumAttributeConverter.create(ServerType::class.java)
    override fun transformFrom(input: ServerType?): AttributeValue = this.converter.transformFrom(input)

    override fun transformTo(input: AttributeValue?): ServerType = this.converter.transformTo(input)

    override fun type(): EnhancedType<ServerType> = this.converter.type()

    override fun attributeValueType(): AttributeValueType = this.converter.attributeValueType()

}
