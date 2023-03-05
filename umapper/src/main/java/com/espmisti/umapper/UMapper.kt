package com.espmisti.umapper

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object UMapper {
    /**
     * Search for identical constructor fields From and To
     *
     * @param To the class from which we will look at similar fields
     * @param Any the class with which we compare the fields
     */
    private fun <To : Class<*>> Any.searchForIdenticalFields(toClazz: To): HashMap<String, Any?> {
        val fromFields = this::class.java.getFieldsConstructor()
        val toFields = toClazz.getFieldsConstructor()
        val resultMap = hashMapOf<String, Any?>()
        for (paramFrom in fromFields) {
            val paramTo = toFields.find { paramTo -> paramTo.name == paramFrom.name }
            if (paramTo != null)
                resultMap[paramTo.name] =
                    this.javaClass.kotlin.memberProperties.first { it.name == paramTo.name }
                        .get(this)
        }
        return resultMap
    }

    /**
     * An automatic mapper that converts data from one data class to another.
     * Classes can be the same in terms of the number of parameters, or different.
     * The mapper searches for the same parameters and, if there are any, converts this data into another class.
     *
     *
     * @param [From]data class from which to collect data
     *
     * @param [To] data class to move all data from [From] to
     * @return [To] data class
     */
    inline fun <From : Any, reified To : Any> From.map(): To {
        return map(fromClazz = this, toClazz = To::class)
    }

    fun <From : Any, To : Any> map(fromClazz: From, toClazz: KClass<To>): To {
        val field = fromClazz.searchForIdenticalFields(toClazz.java)
        val resultMap = mutableMapOf<KParameter, Any?>()
        toClazz.primaryConstructor?.parameters?.map { param ->
            if (field[param.name] != null)
                resultMap[param] = field[param.name]
            else
                if (param.type.isMarkedNullable)
                    resultMap[param] = null
        }
        return toClazz.primaryConstructor?.callBy(resultMap) ?: throw Exception("error")
    }

    /**
     * Getting Constructor fields
     *
     * @param Class to get the fields
     */
    private fun Class<*>.getFieldsConstructor(): List<Field> {
        var clazz: Class<*>? = this
        val fields = mutableListOf<Field>()
        while (clazz != null) {
            fields += clazz.declaredFields
            clazz = clazz.superclass
        }
        return fields
    }
}