/*META
{
    "type": "basic"
}
META*/
SELECT 
    id,
    order_id,
    product_id,
    quantity,
    warehouse_id
FROM order_schema.order_items o
<#if orderId??>
WHERE o.order_id = :orderId
</#if>
<#if limit?? && offset??>
LIMIT :limit OFFSET :offset
</#if>