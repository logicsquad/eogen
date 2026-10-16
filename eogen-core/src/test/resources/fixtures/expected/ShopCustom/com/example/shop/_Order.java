package com.example.shop;

/*
 * Order: com.example.shop.Order (com.example.shop._Order), Order in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=$entity.abstractEntity parent= restricting= keys=[]
 * singleTable=false genericRecord=false plural=Orders table=shop_order
 * primary key: id 
 * attribute 1. currencyCode: java=String class=NSString type= external=varchar width=100 precision= scale= column=currency_code prototype=varchar100 nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 2. customerID: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=customer_id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 3. customerName: java=$attribute.javaClassName class= type= external= width= precision= scale= column= prototype= nulls=$attribute.allowsNull classProperty=true inherited=false flattened=true definition=customer.name
 * attribute 4. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 5. note: java=String class=NSString type= external=text width= precision= scale= column=note prototype=longText nulls=$attribute.allowsNull classProperty=true inherited=false flattened=false definition=
 * attribute 6. placed: java=NSTimestamp class=NSCalendarDate type=T external=timestamp width= precision= scale= column=placed prototype=dateTime nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 7. total: java=java.math.BigDecimal class=NSDecimalNumber type=B external=numeric width= precision=12 scale=2 column=total prototype=amount nulls=false classProperty=true inherited=false flattened=false definition=
 * relationship 1. currency -> Currency (com.example.reference.Currency) toMany=false mandatory=true owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse= classProperty=true inherited=false
 * relationship 2. customer -> Party (com.example.shop.Party) toMany=false mandatory=true owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse=orders classProperty=true inherited=false
 * relationship 3. lines -> OrderLine (com.example.shop.OrderLine) toMany=true mandatory=$relationship.mandatory owns=true propagates=$relationship.propagatesPrimaryKey delete=Cascade flattened=false definition= inverse=order classProperty=true inherited=false
 * fetch byCustomerName: limit= qualifier=customer.name caseInsensitiveLike $name and total > $minimum bindings=1:minimum:java.math.BigDecimal 2:name:String 
 * fetch recent: limit=10 qualifier= bindings=
 * constant after currencyCode: none
 * constant after customerID: none
 * constant after customerName: none
 * constant after id: none
 * constant after note: none
 * constant after placed: none
 * constant after total: none
 * macros: [Order] [Shop] * tools: false true 0
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _Order {
}
