package com.maihaoche.volvo.dao;

/**
 * 作者：yang
 * 时间：17/6/7
 * 邮箱：yangyang@maihaoche.com
 */

public class Enums {

    /**
     * 0和1的枚举
     */
    public enum YesOrNoEnum {
        //成功
        YES(1),
        //失败
        NO(0);

        final int id;

        YesOrNoEnum(int id) {
            this.id = id;
        }


        public static boolean yes(YesOrNoEnum yesOrNoEnum) {
            return yesOrNoEnum != null && yesOrNoEnum == YES;
        }

        public static YesOrNoEnum get(int id) {
            for (YesOrNoEnum status : YesOrNoEnum.values()) {
                if (status.id == id) {
                    return status;
                }
            }
            return NO;
        }

        public static YesOrNoEnum get(boolean yes) {
            return yes ? YES : NO;
        }

        public Integer value() {
            return id;
        }
    }


    /**
     * 盘库结果的枚举
     */
    public enum RecStatus {
        //未完成
        UNCOMPLETE(0),
        //已完成，待上传
        TO_UPLOAD(1),
        //已上传同步
        UPLOADED(2);

        final int id;

        RecStatus(int id) {
            this.id = id;
        }

        public static RecStatus get(int id) {
            for (RecStatus recStatus : RecStatus.values()) {
                if (recStatus.id == id) {
                    return recStatus;
                }
            }
            return UNCOMPLETE;
        }

        public int value() {
            return id;
        }
    }

}
