package com.example.shop;

/*
 * OrderLine: com.example.shop.OrderLine (com.example.shop._OrderLine), OrderLine in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=$entity.abstractEntity parent= restricting= keys=[]
 * singleTable=false genericRecord=false plural=OrderLines table=order_line
 * primary key: id 
 * attribute 1. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 2. orderID: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=order_id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 3. price: java=java.math.BigDecimal class=NSDecimalNumber type=B external=numeric width= precision=12 scale=2 column=price prototype=amount nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 4. productID: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=product_id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 5. quantity: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=quantity prototype=intNumber nulls=false classProperty=true inherited=false flattened=false definition=
 * relationship 1. order -> Order (com.example.shop.Order) toMany=false mandatory=true owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse=lines classProperty=true inherited=false
 * relationship 2. product -> Product (com.example.shop.Product) toMany=false mandatory=true owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse= classProperty=true inherited=false
 * constant after id: none
 * constant after orderID: none
 * constant after price: none
 * constant after productID: none
 * constant after quantity: none
 * macros: [OrderLine] [Shop] * tools: false true 0
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _OrderLine {
}
