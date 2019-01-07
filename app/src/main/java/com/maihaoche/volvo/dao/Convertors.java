package com.maihaoche.volvo.dao;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.UUID;

/**
 * dao中需要用到的类型转换器
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */

public class Convertors {

    public static class AvoidNullIntegerConverter implements PropertyConverter<Integer, Integer> {

        @Override
        public Integer convertToEntityProperty(Integer databaseValue) {
            return databaseValue == null ? Integer.valueOf(0) : databaseValue;
        }

        @Override
        public Integer convertToDatabaseValue(Integer entityProperty) {
            return entityProperty == null ? Integer.valueOf(0) : entityProperty;
        }
    }

    public static class AvoidNullLongConverter implements PropertyConverter<Long, Long> {

        @Override
        public Long convertToEntityProperty(Long databaseValue) {
            return databaseValue == null ? Long.valueOf(0) : databaseValue;
        }

        @Override
        public Long convertToDatabaseValue(Long entityProperty) {
            return entityProperty == null ? Long.valueOf(0) : entityProperty;
        }
    }

    public static class AvoidNullBooleanConverter implements PropertyConverter<Boolean, Boolean> {

        @Override
        public Boolean convertToEntityProperty(Boolean databaseValue) {
            return databaseValue == null ? false : true;
        }

        @Override
        public Boolean convertToDatabaseValue(Boolean entityProperty) {
            return entityProperty == null ? false : true;
        }
    }

    /**
     * {@link UUID} 和 String之间的转换器
     */
    public static class UUIDStringConverter implements PropertyConverter<UUID, String> {

        @Override
        public UUID convertToEntityProperty(String databaseValue) {
            return UUID.fromString(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(UUID entityProperty) {
            return entityProperty.toString();
        }
    }

    /**
     * YesOrNoEnum 的类型转换器
     */
    public static class YesOrNoConverter implements PropertyConverter<Enums.YesOrNoEnum, Integer> {

        @Override
        public Enums.YesOrNoEnum convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return Enums.YesOrNoEnum.get(databaseValue);
        }

        @Override
        public Integer convertToDatabaseValue(Enums.YesOrNoEnum entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }

    /**
     * RecStatus的类型转化器
     */
    public static class RecStatusConvertor implements PropertyConverter<Enums.RecStatus, Integer> {

        @Override
        public Enums.RecStatus convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return Enums.RecStatus.get(databaseValue);
        }

        @Override
        public Integer convertToDatabaseValue(Enums.RecStatus entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }


}
