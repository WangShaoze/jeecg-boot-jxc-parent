package org.jeecg.modules.jxcmanage.entity;

/*
 * ClassName: ExcelColumn
 * Package: org.jeecg.modules.jxcmanage.entity
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/3 - 19:05
 * @Version: v1.0
 */

import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * Excel列定义，用于定制每列的标题、字段名和格式
 */
public class ExcelColumn {
    // 列标题
    private String title;
    // 对应的数据字段名
    private String field;
    // 列宽，-1表示自动宽度
    private int width = -1;
    // 水平对齐方式
    private HorizontalAlignment alignment = HorizontalAlignment.LEFT;
    // 是否加粗
    private boolean bold = false;
    // 数据格式字符串，如"yyyy-MM-dd"
    private String dataFormat;

    // 构造函数
    public ExcelColumn(String title, String field) {
        this.title = title;
        this.field = field;
    }

    // getter和setter方法
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(HorizontalAlignment alignment) {
        this.alignment = alignment;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
}
