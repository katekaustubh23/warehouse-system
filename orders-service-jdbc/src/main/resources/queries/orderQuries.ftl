/*META
{
    "type": "basic"
}
META*/
SELECT 
    o.id AS order_id,
    o.user_id,
    o.status,
    o.order_date,
    o.delivery_address
FROM order_schema.orders o
<#if orderId??>
WHERE o.id = :orderId
</#if>
<#if limit?? && offset??>
LIMIT :limit OFFSET :offset
</#if>