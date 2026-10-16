## A template for eogen's tests. It prints what the model API and Velocity give
## a template, so that the expected output, which WOLips 5.0.3 generated, shows
## any difference.
#if ($entity.superclassPackageName)
package $entity.superclassPackageName;
#end

/*
 * $entity.name: $className ($prefixClassName), $classNameWithoutPackage in $!packageName
 * model $model.name, define "$Greeting"
 * abstract=$entity.abstractEntity parent=$!entity.parent.name restricting=$!entity.restrictingQualifier keys=$entity.restrictingQualifierKeys
 * singleTable=$entity.singleTableInheritance genericRecord=$entity.genericRecord plural=$entity.pluralName table=$entity.externalName
 * primary key: #foreach ($key in $entity.primaryKeyAttributes)$key.name #end

#foreach ($attribute in $entity.sortedAttributes)
 * attribute $velocityCount. $attribute.name: java=$attribute.javaClassName class=$!attribute.valueClassName type=$!attribute.valueType external=$!attribute.externalType width=$!attribute.width precision=$!attribute.precision scale=$!attribute.scale column=$!attribute.columnName prototype=$!attribute.prototype.name nulls=$attribute.allowsNull classProperty=$attribute.classProperty inherited=$attribute.inherited flattened=$attribute.flattened definition=$!attribute.definition
#end
#foreach ($relationship in $entity.sortedRelationships)
 * relationship $velocityCount. $relationship.name -> $relationship.actualDestination.name ($relationship.actualDestination.classNameWithDefault) toMany=$relationship.toMany mandatory=$relationship.mandatory owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=$!relationship.deleteRule.name flattened=$relationship.flattened definition=$!relationship.definition inverse=$!relationship.inverseRelationship.name classProperty=$relationship.classProperty inherited=$relationship.inherited
#end
#foreach ($fetchSpecification in $entity.sortedFetchSpecs)
 * fetch $fetchSpecification.name: limit=$!fetchSpecification.fetchLimit qualifier=$!fetchSpecification.qualifier bindings=#foreach ($binding in $fetchSpecification.distinctBindings)$velocityCount:$binding.name:$binding.attributePath.childClassName #end

#end
#set ($constant = "none")
#foreach ($attribute in $entity.sortedAttributes)
#set ($constant = $attribute.userInfo.ERXConstantClassName)
 * constant after $attribute.name: $constant
#end
#macro (describe $thing)[$thing.name]#end
 * macros: #describe($entity) #describe($model)
 * tools: $list.isList($entity.sortedAttributes) $set.isSet($entity.restrictingQualifierKeys) $set.size($entity.restrictingQualifierKeys)
 * hyphens: #set ($with-hyphen = "allowed")$with-hyphen
 * empty is true: #if ($entity.restrictingQualifierKeys)yes#else no#end
 * no argument conversion: $entity.name.substring("1")
 */
public abstract class $prefixClassNameWithoutPackage {
}
