/* ------------------------------------------------------------------------------------------------------------
• FoscCyclicArray

Cylic array.

Cyclic arrays overload the item-getting method of built-in arrays.

Cyclic arrays return a value for any integer index.

Cyclic arrays otherwise behave exactly like built-in arrays.

a = FoscCyclicArray(#[a, b, c, d]);
(0..12).do { |i| a[i].postln };
------------------------------------------------------------------------------------------------------------ */
FoscCyclicArray : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    __slots__ = (
        '_items',
        )

    def __init__(self, items=None):
        items = items or ()
        items = tuple(items)
        self._items = items
    -------------------------------------------------------------------------------------------------------- */
    var <items;
    *new { |items|
        items = items ? [];
        ^super.new.init(items);
    }
    init { |argItems|
        items = argItems;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • includes (abjad: container)

    Is true when cyclic tuple contains item.

    Returns true or false.
    
    def __contains__(self, item):
        return self._items.__contains__(item)
    -------------------------------------------------------------------------------------------------------- */
    includes { |item|
        ^items.includes(item);
    }
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when argument is a tuple with items equal to those of this cyclic tuple. Otherwise false.
    
    Returns true or false.
    
    def __eq__(self, argument):
        if isinstance(argument, tuple):
            return self._items == argument
        elif isinstance(argument, type(self)):
            return self._items == argument._items
        return False
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        if (expr.isKindOf(Array)) { ^(items == expr) };
        if (expr.isKindOf(this.species)) { ^(items == expr.items) };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at

    Gets item or slice identified by argument. Returns nil when no item exists at index.

    Returns item.
    
    def __getitem__(self, argument):
        if isinstance(argument, slice):
            if ((argument.stop is not None and argument.stop < 0) or
                (argument.start is not None and argument.start < 0)):
                return self._items.__getitem__(argument)
            else:
                return self._get_slice(argument.start, argument.stop)
        if not self:
            message = 'cyclic tuple is empty: {!r}.'
            message = message.format(self)
            raise IndexError(message)
        argument = argument % len(self)
        return self._items.__getitem__(argument)
    -------------------------------------------------------------------------------------------------------- */
    at { |index|
        if (index.isSequenceableCollection) { ^this.atAll(index) };
        ^items.wrapAt(index);
    }
    /* --------------------------------------------------------------------------------------------------------
    • atAll
    -------------------------------------------------------------------------------------------------------- */
    atAll { |indices|
        var result;
        result = [];
        indices.do { |index| result = result.add(items.wrapAt(index)) };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • copySeries (abjad: _get_slice)
    
    def _get_slice(self, start_index, stop_index):
        if stop_index is not None and 1000000 < stop_index:
            stop_index = len(self)
        result = []
        if start_index is None:
            start_index = 0
        if stop_index is None:
            indices = range(start_index, len(self))
        else:
            indices = range(start_index, stop_index)
        result = [self[n] for n in indices]
        return tuple(result)
    
    a = FoscCyclicArray(#[a,b,c,d,e,f]);
    a[0..12];

    a = FoscCyclicArray(#[a,b,c,d,e,f]);
    a[..12];

    a = FoscCyclicArray(#[a,b,c,d,e,f]);
    a[3..17];

    a = FoscCyclicArray(#[a,b,c,d,e,f]);
    a[0, 2 .. 15];
    -------------------------------------------------------------------------------------------------------- */
    copySeries { |first, second, last|
        var result, indices;
        if (first.isNil) { first = 0 };
        if (second.isNil) { second = first + 1 };
        if (last.isNil) { last = this.size };
        result = [];
        indices = (first, second .. last);
        indices.do { |index| result = result.add(items.wrapAt(index)) };
        ^result; 
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes cyclic tuple.

    Returns integer.
    
    def __hash__(self):
        return super(CyclicTuple, self).__hash__()
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • iter

    Iterates cyclic tuple. Iterates items only once.

    Does not iterate infinitely.
    
    def __iter__(self):
        return self._items.__iter__()
    -------------------------------------------------------------------------------------------------------- */
    iter {
        ^items.iter;
    }
    /* --------------------------------------------------------------------------------------------------------
    • size

    Gets length of cyclic tuple.

    Returns nonnegative integer.
     
    def __len__(self):
        assert isinstance(self._items, tuple)
        return self._items.__len__()
    -------------------------------------------------------------------------------------------------------- */
    size {
        ^items.size;
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of cyclic tuple.

    Returns string.

    
• Example ---

        Gets string:

        ::

            >>> str(abjad.CyclicTuple('abcd'))
            '(a, b, c, d)'

    
• Example ---

        Gets string:

        ::

            >>> str(abjad.CyclicTuple([1, 2, 3, 4]))
            '(1, 2, 3, 4)'

    
    def __str__(self):
        if self:
            contents = [str(item) for item in self._items]
            contents = ', '.join(contents)
            string = '({!s})'.format(contents)
            return string
        return '()'
    -------------------------------------------------------------------------------------------------------- */
    str {
        var contents, string;
        if (items.notEmpty) {
            contents = items.collect { |item| item.str };
            contents = contents.join(", ");
            string = "[%]".format(contents);
        };
        ^#[];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatSpecification
    
    def _get_format_specification(self):
        from abjad.tools import systemtools
        return systemtools.FormatSpecification(
            client=self,
            repr_is_indented=False,
            storage_format_args_values=[list(self._items)],
            )
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatSpecification {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • items

    Gets items in cyclic tuple.

    Returns array.
    
    @property
    def items(self):
        return self._items
    -------------------------------------------------------------------------------------------------------- */
}
