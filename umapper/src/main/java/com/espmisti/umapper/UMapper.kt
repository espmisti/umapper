package com.espmisti.umapper

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
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
        val response = mutableMapOf<KParameter, Any?>()
        val map = fromClazz.searchForIdenticalFields(toClazz.java)
        val parameters = toClazz.primaryConstructor?.parameters
        parameters?.map { param ->
            val instance = map[param.name]
            if (instance != null) {
                when (instance) {
                    is List<*> -> {
                        val mutableList = mutableListOf<Any?>()
                        instance.forEach {
                            if (it != null) mutableList.add(it)
                            else mutableList.add(null)
                        }
                        response[param] = mutableList
                    }
                    else -> response[param] = instance
                }
            } else {
                if (param.type.isMarkedNullable)
                    response[param] = null
                else
                    throw MapperException(message = "The field that came from From is null, and the one you want to pass to is Non-Nullable")
            }
        }
        return if (response.isNotEmpty()) toClazz.primaryConstructor?.callBy(response) ?: throw MapperException(message = "Mapper returned an error")
        else toClazz.createInstance()
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