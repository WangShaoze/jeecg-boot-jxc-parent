package org.jeecg.modules.jxcmanage.vo;

/*
 * ClassName: dsds
 * Package: org.jeecg.modules.jxcmanage.entity
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/19 - 16:37
 * @Version: v1.0
 */

import com.alipay.api.domain.ItemInfo;
import lombok.Data;
import org.jeecg.modules.jxcmanage.entity.TBQueryProduct;

/**
 *  商品查询表
 */
@Data
public class ItemInfoVO {
    // 证书编号
    private String certificateNumber;
    // 商品名称
    private String productName;
    // 形状
    private String shape;
    // 颜色
    private String color;
    // 基材重
    private Double baseMaterialWeight;
    // 电金后重
    private Double weightAfterGoldPlating;
    // 总质量
    private Double totalWeight;
    // 金重
    private Double goldWeight;

    public static ItemInfoVO create(TBQueryProduct queryProduct) {
        ItemInfoVO vo = new ItemInfoVO();
        vo.setCertificateNumber(queryProduct.getCertificateNumber());
        vo.setProductName("镀金银" + queryProduct.getProductName());
        vo.setShape(queryProduct.getShape());
        vo.setColor(queryProduct.getColor());
        vo.setBaseMaterialWeight(queryProduct.getBaseMaterialWeight());
        vo.setWeightAfterGoldPlating(queryProduct.getWeightAfterGoldPlating());
        vo.setTotalWeight(queryProduct.getTotalWeight());
        vo.setGoldWeight(queryProduct.getGoldWeight());
        return vo;
    }
}
