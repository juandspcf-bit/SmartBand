package com.misawabus.project.heartRate;

/**
 * Created by Administrator on 2017/11/25.
 */

public interface Oprate {
    String PWD_COMFIRM = "PWD_COMFIRM_1、设备密码-验证";
    String PERSONINFO_SYNC = "2、个人信息-设置";
    String SETTING_FIRST = "<--先操作1、2";
    String PWD_MODIFY = "PWD_MODIFY_设备密码-修改";
    String HEART_DETECT_START = "HEART_DETECT_START_测量心率-开始";
    String HEART_DETECT_STOP = "HEART_DETECT_STOP_测量心率-结束";
    String BP_DETECT_START = "BP_DETECT_START_测量血压-开始";
    String BP_DETECT_STOP = "BP_DETECT_STOP测量血压-结束";
    String BP_DETECTMODEL_SETTING = "血压模式-设置";
    String BP_DETECTMODEL_SETTING_ADJUSTE = "血压模式[动态调整]-设置";
    String BP_DETECTMODEL_SETTING_ADJUSTE_CANCEL = "血压模式[动态调整]-取消";
    String BP_DETECTMODEL_READ = "血压模式-读取";
    String SPORT_CURRENT_READ = "当前计步-读取";
    String CAMERA_START = "CAMERA_START_拍照模式-开始";
    String CAMERA_STOP = "CAMERA_STOP-拍照模式-停止";
    String ALARM_SETTING = "闹钟-设置";
    String ALARM_READ = "闹钟-读取";
    String ALARM_NEW_READ = "新闹钟-读取";
    String ALARM_NEW_ADD = "新闹钟-添加";
    String ALARM_NEW_MODIFY = "新闹钟-修改";
    String ALARM_NEW_DELETE = "新闹钟-删除";
    String LONGSEAT_SETTING_OPEN = "久坐-打开";
    String LONGSEAT_SETTING_CLOSE = "久坐-关闭";
    String LONGSEAT_READ = "久坐-读取";
    String LANGUAGE_CHINESE = "LANGUAGE_CHINESE_语言设置-中文";
    String LANGUAGE_ENGLISH = "LANGUAGE_ENGLISH_语言设置-英文";
    String BATTERY = "BATTERY-电池状态-读取";
    String NIGHT_TURN_WRIST_OPEN = "夜间转腕-打开";
    String NIGHT_TURN_WRIST_CLOSE = "夜间转腕-关闭";
    String NIGHT_TURN_WRIST_READ = "夜间转腕-读取";
    String NIGHT_TURN_WRIST_CUSTOM_TIME = "夜间转腕-自定义时间";
    String NIGHT_TURN_WRIST_CUSTOM_TIME_LEVEL = "夜间转腕-自定义时间和等级";
    String FINDPHONE = "手机防丢";
    String CHECK_WEAR_SETING_OPEN = "佩戴检测-打开";
    String CHECK_WEAR_SETING_CLOSE = "佩戴检测-关闭";
    String FINDDEVICE_SETTING_OPEN = "设备防丢-打开";
    String FINDDEVICE_SETTING_CLOSE = "设备防丢-关闭";
    String FINDDEVICE_READ = "设备防丢-读取";
    String DEVICE_COUSTOM_READ = "DEVICE_COUSTOM_READ_个性化-读取";
    String DEVICE_COUSTOM_SETTING = "个性化-设置";
    String SOCIAL_MSG_SETTING = "社交消息提醒-设置";
    String SOCIAL_MSG_READ = "社交消息提醒-读取设置";
    String SOCIAL_MSG_SEND = "社交消息提醒-发送内容";
    String SOCIAL_PHONE_IDLE_OR_OFFHOOK = "社交消息提醒-接听了来电";
    String DEVICE_CONTROL_PHONE = "监听手环-挂断，静音";
    String HEARTWRING_READ = "心率报警-读取";
    String HEARTWRING_OPEN = "心率报警-打开";
    String HEARTWRING_CLOSE = "心率报警-关闭";
    String SPO2H_OPEN = "血氧-读取";
    String SPO2H_CLOSE = "血氧-结束";
    String SPO2H_AUTO_DETECT_READ = "血氧自动检测-读取";
    String SPO2H_AUTO_DETECT_OPEN = "血氧自动检测-打开";
    String SPO2H_AUTO_DETECT_CLOSE = "血氧自动检测-关闭";
    String FATIGUE_OPEN = "FATIGUE_OPEN_疲劳度-读取";
    String FATIGUE_CLOSE = "FATIGUE_CLOSE_疲劳度-结束";
    String WOMEN_SETTING = "女性状态-设置";
    String WOMEN_READ = "女性状态-读取";
    String COUNT_DOWN_WATCH = "倒计时-手表单独使用";
    String COUNT_DOWN_APP = "倒计时-App使用";
    String COUNT_DOWN_APP_READ = "倒计时-读取";
    String SCREEN_LIGHT_SETTING = "屏幕调节-设置";
    String SCREEN_LIGHT_READ = "屏幕调节-读取";
    String SCREEN_STYLE_READ = "屏幕样式-读取";
    String SCREEN_STYLE_SETTING = "SCREEN_STYLE_SETTING_屏幕样式-设置";
    String AIM_SPROT_CALC = "目标步数-计算";
    String INSTITUTION_TRANSLATION = "INSTITUTION_TRANSLATION-公英制转换";
    String READ_HEALTH_DRINK = "读取健康数据-饮酒";
    String READ_HEALTH_SLEEP = "READ_HEALTH_SLEEP-读取健康数据-睡眠";
    String READ_HEALTH_SLEEP_FROM = "READ_HEALTH_SLEEP_FROM-读取健康数据-睡眠-从哪天起";
    String READ_HEALTH_SLEEP_SINGLEDAY = "READ_HEALTH_SLEEP_SINGLEDAY-读取健康数据-睡眠-读这天";
    String READ_HEALTH_ORIGINAL = "READ_HEALTH_ORIGINAL-读取健康数据-5分钟";
    String READ_HEALTH_ORIGINAL_FROM = "READ_HEALTH_ORIGINAL_FROM_读取健康数据-从哪天起";
    String READ_HEALTH_ORIGINAL_SINGLEDAY = "READ_HEALTH_ORIGINAL_SINGLEDAY_读取健康数据-读这天";
    String READ_HEALTH = "READ_HEALTH_读取健康数据-全部";
    String OAD = "固件升级";
    String SHOW_SP = "显示sp";
    String SPORT_MODE_ORIGIN_READ = "SPORT_MODE_ORIGIN_READ_读取数据-运动模式";
    String SPORT_MODE_ORIGIN_READSTAUTS = "读取状态-运动模式";
    String SPORT_MODE_ORIGIN_START = "开启-运动模式";
    String SPORT_MODE_ORIGIN_END = "结束-运动模式";
    String SPO2H_ORIGIN_READ = "读取数据-血氧数据";
    String HRV_ORIGIN_READ = "HRV_ORIGIN_READ-读取数据-HRV数据";
    String CLEAR_DEVICE_DATA = "CLEAR_DEVICE_DATA-清除数据";
    String DISCONNECT = "DISCONNECT_蓝牙连接-断开";
    String DETECT_PTT = "PTT";
    String DETECT_START_ECG = "开始测量ECG";
    String DETECT_STOP_ECG = "结束测量ECG";
    String LOW_POWER_READ = "低功耗-读取";
    String LOW_POWER_OPEN = "低功耗-开";
    String LOW_POWER_CLOSE = "低功耗-关";
    String S22_READ_DATA = "S22-数据读取";
    String S22_READ_STATE = "S22-状态读取";
    String S22_SETTING_STATE_OPEN = "S22-状态设置(开)";
    String S22_SETTING_STATE_CLOSE = "S22-状态设置(关)";
    String NONE = "NONE";
    String[] oprateStr = new String[]{
            PWD_COMFIRM, PERSONINFO_SYNC, SETTING_FIRST, PWD_MODIFY,
            HEART_DETECT_START, HEART_DETECT_STOP, BP_DETECT_START, BP_DETECT_STOP, BP_DETECTMODEL_SETTING, BP_DETECTMODEL_READ,
            BP_DETECTMODEL_SETTING_ADJUSTE_CANCEL, BP_DETECTMODEL_SETTING_ADJUSTE,
            SPORT_CURRENT_READ, CAMERA_START, CAMERA_STOP, ALARM_SETTING, ALARM_READ, ALARM_NEW_READ, ALARM_NEW_ADD, ALARM_NEW_MODIFY, ALARM_NEW_DELETE,
            LONGSEAT_SETTING_OPEN, LONGSEAT_SETTING_CLOSE, LONGSEAT_READ, LANGUAGE_CHINESE, LANGUAGE_ENGLISH,
            BATTERY, NIGHT_TURN_WRIST_OPEN, NIGHT_TURN_WRIST_CLOSE, NIGHT_TURN_WRIST_READ, NIGHT_TURN_WRIST_CUSTOM_TIME, NIGHT_TURN_WRIST_CUSTOM_TIME_LEVEL,
            DEVICE_COUSTOM_READ, DEVICE_COUSTOM_SETTING, FINDPHONE,
            CHECK_WEAR_SETING_OPEN, CHECK_WEAR_SETING_CLOSE,
            FINDDEVICE_SETTING_OPEN, FINDDEVICE_SETTING_CLOSE, FINDDEVICE_READ,
            SOCIAL_MSG_SETTING, SOCIAL_MSG_READ, SOCIAL_MSG_SEND, DEVICE_CONTROL_PHONE, SOCIAL_PHONE_IDLE_OR_OFFHOOK, HEARTWRING_READ, HEARTWRING_OPEN, HEARTWRING_CLOSE,
            SPO2H_OPEN, SPO2H_CLOSE, SPO2H_AUTO_DETECT_READ, SPO2H_AUTO_DETECT_OPEN, SPO2H_AUTO_DETECT_CLOSE, FATIGUE_OPEN, FATIGUE_CLOSE, WOMEN_SETTING, WOMEN_READ, COUNT_DOWN_WATCH, COUNT_DOWN_APP, COUNT_DOWN_APP_READ, SCREEN_LIGHT_SETTING, SCREEN_LIGHT_READ, SCREEN_STYLE_READ, SCREEN_STYLE_SETTING, AIM_SPROT_CALC, INSTITUTION_TRANSLATION,
            READ_HEALTH_SLEEP, READ_HEALTH_SLEEP_FROM, READ_HEALTH_SLEEP_SINGLEDAY, READ_HEALTH_DRINK, READ_HEALTH_ORIGINAL,
            READ_HEALTH_ORIGINAL_FROM, READ_HEALTH_ORIGINAL_SINGLEDAY, READ_HEALTH,
            OAD, SHOW_SP, SPORT_MODE_ORIGIN_READ, SPORT_MODE_ORIGIN_READSTAUTS, SPORT_MODE_ORIGIN_START, SPORT_MODE_ORIGIN_END, SPO2H_ORIGIN_READ, HRV_ORIGIN_READ, CLEAR_DEVICE_DATA, DISCONNECT
            , DETECT_START_ECG, DETECT_STOP_ECG, NONE, LOW_POWER_READ, LOW_POWER_OPEN, LOW_POWER_CLOSE,S22_READ_DATA,S22_READ_STATE,S22_SETTING_STATE_OPEN,S22_SETTING_STATE_CLOSE,DETECT_PTT
    };
}
