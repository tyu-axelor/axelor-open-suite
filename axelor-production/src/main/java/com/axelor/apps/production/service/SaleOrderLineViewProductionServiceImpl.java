package com.axelor.apps.production.service;

import com.axelor.apps.account.service.analytic.AnalyticAttrsService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.supplychain.service.SaleOrderLineViewSupplychainServiceImpl;
import com.axelor.apps.supplychain.service.analytic.AnalyticAttrsSupplychainService;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SaleOrderLineViewProductionServiceImpl
    extends SaleOrderLineViewSupplychainServiceImpl {

  @Inject
  public SaleOrderLineViewProductionServiceImpl(
      AnalyticAttrsService analyticAttrsService,
      AnalyticAttrsSupplychainService analyticAttrsSupplychainService) {
    super(analyticAttrsService, analyticAttrsSupplychainService);
  }

  @Override
  public Map<String, Map<String, Object>> getOnNewAttrs(
      SaleOrderLine saleOrderLine, SaleOrder saleOrder) throws AxelorException {
    Map<String, Map<String, Object>> attrs = super.getOnNewAttrs(saleOrderLine, saleOrder);
    attrs.putAll(hideBomAndProdProcess(saleOrderLine));
    return attrs;
  }

  @Override
  public Map<String, Map<String, Object>> getOnLoadAttrs(
      SaleOrderLine saleOrderLine, SaleOrder saleOrder) throws AxelorException {
    Map<String, Map<String, Object>> attrs = super.getOnLoadAttrs(saleOrderLine, saleOrder);
    attrs.putAll(hideBomAndProdProcess(saleOrderLine));
    return attrs;
  }

  @Override
  public Map<String, Map<String, Object>> getProductOnChangeAttrs(
      SaleOrderLine saleOrderLine, SaleOrder saleOrder) throws AxelorException {
    Map<String, Map<String, Object>> attrs =
        super.getProductOnChangeAttrs(saleOrderLine, saleOrder);
    attrs.putAll(hideBomAndProdProcess(saleOrderLine));
    return attrs;
  }

  @Override
  public Map<String, Map<String, Object>> getSaleSupplySelectOnChangeAttrs(
      SaleOrderLine saleOrderLine, SaleOrder saleOrder) {
    Map<String, Map<String, Object>> attrs =
        super.getSaleSupplySelectOnChangeAttrs(saleOrderLine, saleOrder);
    attrs.putAll(hideBomAndProdProcess(saleOrderLine));
    return attrs;
  }

  protected Map<String, Map<String, Object>> hideBomAndProdProcess(SaleOrderLine saleOrderLine) {
    Map<String, Map<String, Object>> attrs = new HashMap<>();
    int saleSupplySelect = saleOrderLine.getSaleSupplySelect();
    String productTypeSelect =
        Optional.ofNullable(saleOrderLine.getProduct())
            .map(Product::getProductTypeSelect)
            .orElse("");
    boolean hideBom =
        (saleSupplySelect != ProductRepository.SALE_SUPPLY_PRODUCE
                && saleSupplySelect != ProductRepository.SALE_SUPPLY_FROM_STOCK_AND_PRODUCE)
            || productTypeSelect.equals(ProductRepository.PRODUCT_TYPE_SERVICE);
    boolean hideProdProcess =
        saleSupplySelect != ProductRepository.SALE_SUPPLY_PRODUCE
            || productTypeSelect.equals(ProductRepository.PRODUCT_TYPE_SERVICE);
    attrs.put("billOfMaterial", Map.of(HIDDEN_ATTR, hideBom));
    attrs.put("customizeBOMBtn", Map.of(HIDDEN_ATTR, hideBom));
    attrs.put("prodProcess", Map.of(HIDDEN_ATTR, hideProdProcess));
    attrs.put("customizeProdProcessBtn", Map.of(HIDDEN_ATTR, hideProdProcess));
    return attrs;
  }
}
