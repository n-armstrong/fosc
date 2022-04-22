/* ------------------------------------------------------------------------------------------------------------
• FoscTypedCollection

x = FoscTypedCollection([1, 2, 3, 4], Number);

x = FoscTypedCollection([1, 2, 3, 4, 'x'], Number);
------------------------------------------------------------------------------------------------------------ */
FoscTypedCollection : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <collection, <itemClass;
	*new { |items, itemClass|
        if (items.isKindOf(this)) {
            items = items.items;
        } {
            if (itemClass.isNil) { ^throw("%: itemClass cannot be nil.".format(this.name)) };

            items.do { |item| 
                if (item.isKindOf(itemClass).not) {
                    ^throw("%:new: item % is the wrong type: %.".format(this.species, item, item.species));  
                };  
            };
        };
        ^super.new.init(items, itemClass);
	}
	init { |items, argItemClass|
        collection = items;
        itemClass = argItemClass;
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: COMPARISON
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
    
    a = FoscTypedCollection([1, 2, 3, 4], Number);
    b = FoscTypedCollection([1, 2], Number);
    a == b;
    a == a.copy;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        ^((this.species == expr.species) && (collection == expr.collection));
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=
    
    a = FoscTypedCollection([1, 2, 3, 4], Number);
    b = FoscTypedCollection([1, 2], Number);
    a != b;
    a != a.copy;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(10, 20),
        FoscTimespan(30, 40)
    ]);

    t.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        if (this.isEmpty) {
            ^"%([])".format(this.species);
        } {
            ^"%([\n\t%\n])".format(this.species, this.items.collect { |each| each.cs }.join(",\n\t"));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • difference

    Set-theoretic difference of receiver and expr.

    Returns new typed frozen set.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([2, 3, 4], Number);
    x = a.difference(b); // x = (a - b);
    x.inspect;

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.difference(b).do { |each| each.pitchClassNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    difference { |expr|
        var result;
        //### assert this and expr are of same type
        result = this.items.copy;
        this.do { |item|
            if (expr.items.includesEqual(item)) { result.remove(item) };
        };
        ^this.species.new(result, this.itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Set-theoretic intersection of receiver and expr.

    Returns new typed frozen set.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([2, 3, 4], Number);
    x = sect(a, b); // x = (a & b);
    x.inspect;

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.sect(b).do { |each| each.pitchClassNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    sect { |expr|
        var result;
        //### assert this and expr are of same type
        result = [];
        this.do { |item|
            if (expr.items.includesEqual(item)) { result = result.add(item) };
        };
        ^this.species.new(result, this.itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isDisjoint

    Is true when typed receiver shares no elements with expr. Otherwise false.

    Returns boolean.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([4], Number);
    c = FoscTypedCollection([3, 4], Number);
    isDisjoint(a, b);
    isDisjoint(a, c);
    -------------------------------------------------------------------------------------------------------- */
    isDisjoint { |expr|
        //### assert this and expr are of same type
        ^this.sect(expr).isEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    Is true when set is empty.

    Returns boolean.

    a = FoscTypedCollection([1, 2, 3], Number);
    a.isEmpty;

    a = FoscTypedCollection([], Number);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    isEmpty {
        ^this.items.isEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSubsetOf

    Is true when receiver is a subset of expr. Otherwise false.

    Returns boolean.


    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([4], Number);
    c = FoscTypedCollection([1, 2, 3, 4], Number);
    b.isSubsetOf(a);
    a.isSubsetOf(c);

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3]);
    a.isSubsetOf(b);
    b.isSubsetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    isSubsetOf { |expr|
        //### assert this and expr are of same type
        var items;
        items = expr.items;
        this.do { |each|
            if (items.includesEqual(each).not) { ^false };
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSupersetOf

    Is true when receiver is a superset of expr. Otherwise false.

    Returns boolean.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([4], Number);
    c = FoscTypedCollection([1, 2, 3, 4], Number);
    b.isSupersetOf(a);
    c.isSupersetOf(a);

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3]);
    a.isSupersetOf(b);
    b.isSupersetOf(a);
    -------------------------------------------------------------------------------------------------------- */
    isSupersetOf { |expr|
        //### assert this and expr are of same type
        var items;
        items = this.items;
        expr.do { |each|
            if (items.includesEqual(each).not) { ^false };
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    Is true when set is not empty.

    Returns boolean.

    a = FoscTypedCollection([1, 2, 3], Number);
    a.notEmpty;

    a = FoscTypedCollection([], Number);
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    notEmpty {
        ^this.items.notEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Symmetric difference of receiver and expr.

    Returns new typed frozen set.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([2, 3, 4], Number);
    x = symmetricDifference(a, b); // a -- b;
    x.inspect;

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.symmetricDifference(b).do { |each| each.pitchClassNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    symmetricDifference { |expr|
        var result;
        //### assert this and expr are of same type
        result = [];
        this.do { |item|
            if (expr.items.includesEqual(item).not) { result = result.add(item) };
        };
        expr.do { |item|
            if (this.items.includesEqual(item).not) { result = result.add(item) };
        };
        ^this.species.new(result, this.itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union of receiver and expr.

    Returns new typed frozen set.

    a = FoscTypedCollection([1, 2, 3], Number);
    b = FoscTypedCollection([2, 3, 4], Number);
    x = union(a, b); // x = (a | b);
    x.inspect;

    a = FoscPitchClassSet([1, 2, 3]);
    b = FoscPitchClassSet([2, 3, 4]);
    a.union(b).do { |each| each.pitchClassNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    union { |expr|
        var result;
        //### assert this and expr are of same type
        result = this.items.copy;
        expr.items.do { |each|
            if (result.includesEqual(each).not) { result = result.add(each) };
        };
        ^this.species.new(result, this.itemClass);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: ENUMERATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • do

    x = FoscTypedCollection([1, 2, 3, 4], Number);
    x.do { |each| (each * 2).postln };
    -------------------------------------------------------------------------------------------------------- */
    do { |func|
        ^this.items.do(func);
    }
    /* --------------------------------------------------------------------------------------------------------
    • iter
    
    a = FoscTypedCollection([1, 2, 3, 4], Number);
    a = a.iter;
    5.do { a.next.postln };
    -------------------------------------------------------------------------------------------------------- */
    iter {
        ^collection.iter;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • includes
    
    a = FoscTypedCollection([1, 2, 3, 4], Number);
    a.includes(4);
    -------------------------------------------------------------------------------------------------------- */
    includes { |item|
        ^collection.includesEqual(item);
    }
    /* --------------------------------------------------------------------------------------------------------
    • indexOf

    a = FoscTypedCollection([1, 2, 3, 4], Number);
    a.indexOf(3);

    a = FoscTypedCollection([1, 2, 3, 4].collect { |each| FoscPitchClass(each) }, FoscPitchClass);
    a.indexOf(3);
    -------------------------------------------------------------------------------------------------------- */
    indexOf { |item|
        this.items.do { |elem, i| if (item == elem ) { ^i } };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isCollection

    a = FoscTypedCollection([1, 2, 3, 4], Number);
    a.isCollection;
    -------------------------------------------------------------------------------------------------------- */
    isCollection {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • items
    
    x = FoscTypedCollection([1, 2, 3, 4], Number);
    x.items;
    -------------------------------------------------------------------------------------------------------- */
    items {
        ^collection.asArray;
    }
    /* --------------------------------------------------------------------------------------------------------
    • size
    
    Size of typed collection.

    Returns nonnegative integer.
    
    a = FoscTypedCollection([1, 2, 3, 4], Number);
    a.size;
    -------------------------------------------------------------------------------------------------------- */
    size {
        ^collection.size;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • prItemCoercer
    -------------------------------------------------------------------------------------------------------- */
    prItemCoercer { |item|
        ^if (item.isKindOf(itemClass)) { item } { itemClass.new(item) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prOnInsertion
    
    Override to operate on item after insertion into collection.
    -------------------------------------------------------------------------------------------------------- */
    prOnInsertion { |item|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prOnRemoval
    
    Override to operate on item after removal from collection.
    -------------------------------------------------------------------------------------------------------- */
    prOnRemoval { |item|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    def _get_format_specification(self):
        agent = systemtools.StorageFormatAgent(self)
        names = list(agent.signature_keyword_names)
        if 'items' in names:
            names.remove('items')
        return systemtools.FormatSpecification(
            self,
            repr_is_indented=False,
            storage_format_args_values=[self._collection],
            storage_format_kwargs_names=names,
            )
    -------------------------------------------------------------------------------------------------------- */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        collection.do { |each| each.postln };
    }
}
