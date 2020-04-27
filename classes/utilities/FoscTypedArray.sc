/* ------------------------------------------------------------------------------------------------------------
• FoscTypedArray

x = FoscTypedArray([1, 2, 3, 4], Number);
x.inspect;
------------------------------------------------------------------------------------------------------------ */
FoscTypedArray : FoscTypedSequenceableCollection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    init { |items, argItemClass|
        collection = items ?? { [] };
        itemClass = argItemClass;
        collection = collection.collect { |item| this.prItemCoercer(item) }; // coerce type
        collection = collection.asArray;
    }   
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: LIST MODIFICATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ++
    
    Concatenates typed list and expr.

    Returns new typed list.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    b = FoscTypedArray([5, 6], Number);
    (a ++ b).items;

    a = FoscTypedArray([1, 2, 3, 4], Number);
    b = [5, 6]; // coerce to itemClass
    (a ++ b).items;
    -------------------------------------------------------------------------------------------------------- */
    ++ { |expr|
        var items;
        expr = this.species.new(expr, itemClass);
        items = this.items ++ expr.items;
        ^this.species.new(items, itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • append

    Appends item to typed array.

    Returns new typed array.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a = a.add(5);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    add { |item|
        var items;
        item = this.prItemCoercer(item);
        items = this.items.add(item);
        this.prOnInsertion(item);
        ^this.species.new(items, itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • addAll

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a = a.addAll([5, 6, 7]);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    // addAll { |items|
    //     items.do { |item| this.add(item) };
    // }
    /* --------------------------------------------------------------------------------------------------------
    • insert

    Insert item at index.

    Returns new typed array.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a = a.insert(2, 99);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    insert { |index, item|
        var items;
        item = this.prItemCoercer(item);
        items = this.items.insert(index, item);
        this.prOnInsertion(item);
        ^this.species.new(items, itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prepend

    Prepends item to typed array.

    Returns new typed array.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a = a.prepend(5);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    prepend { |item|
        ^this.insert(0, item);
    }
    /* --------------------------------------------------------------------------------------------------------
    • put

    Put item at index.

    Returns new typed array.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a = a.put(2, 99);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    put { |index, item|
        var items, oldItem;
        item = this.prItemCoercer(item);
        items = this.items.put(index, item);
        oldItem = collection[index];
        this.prOnInsertion(item);
        this.prOnRemoval(oldItem);
        ^this.species.new(items, itemClass);
    }
    /* --------------------------------------------------------------------------------------------------------
    • remove

    Remove item.

    Returns removed item.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.remove(2);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • removeAt

    Remove item at index.

    Returns removed item.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.removeAt(1);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sort

    Sort the contents of the collection using optional comparison function.

    Returns new typed array.

    a = FoscTypedArray([5, 2, 3, 4], Number);
    a = a.sort;
    a.items;

    a = FoscTypedArray([5, 2, 3, 4], Number);
    a = a.sort { |a, b| a > b };
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    sort { |func|
        var items;
        items = this.items.copy.sort(func);
        ^this.species.new(items, itemClass);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • at
    
    Gets item at index.

    Returns item.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a[2];
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • atAll
    
    Gets items at indices.

    Returns items.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a[(2..3)];
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • includes
    
    Answer true if item exists in collection.

    Returns boolean.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.includes(3);
    a.includes(5);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • indexOf (abjad: index)
    
    Return the first index matching item.

    Returns nonnegative integer.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.indexOf(3);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • occurrencesOf (abjad: count)
    
    Return the number of occurrences of item in collection.

    Returns nonnegative integer.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.occurrencesOf(3);
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect

    Inspect items in collection.

    a = FoscTypedArray([1, 2, 3, 4], Number);
    a.inspect;
    -------------------------------------------------------------------------------------------------------- */
}
