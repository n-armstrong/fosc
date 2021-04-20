/* ------------------------------------------------------------------------------------------------------------
• FoscPattern

Pattern.


• Example 1

Matches three indices in every eight.

p = FoscPattern(#[0,1,7], period: 8);
n = 16;
n.do { |i|
    m = p.matchesIndex(i, n);
    if (m.not) { m = "" };
    Post << i << Char.space << m << nl;
};


Matches three indices in every sixteen.

p = FoscPattern(#[0,1,7], period: 16);
n = 16;
n.do { |i|
    m = p.matchesIndex(i, n);
    if (m.not) { m = "" };
    Post << i << Char.space << m << nl;
};


Works with improper indices.

p = FoscPattern(#[16,17,23], period: 16);
n = 16;
n.do { |i|
    m = p.matchesIndex(i, n);
    if (m.not) { m = "" };
    Post << i << Char.space << m << nl;
};


• Example 2

Sieve from opening of Xenakis’s Psappha.

~sieve_1a = FoscPattern.indices(#[0,1,7], 8);
~sieve_1b = FoscPattern.indices(#[1,3], 5);
~sieve_1 = ~sieve_1a & ~sieve_1b;
~sieve_2a = FoscPattern.indices(#[0,1,2], 8);
~sieve_2b = FoscPattern.indices(#[0], 5);
~sieve_2 = ~sieve_2a & ~sieve_2b;
~sieve_3 = FoscPattern.indices(#[3], 8);
~sieve_4 = FoscPattern.indices(#[4], 8);
~sieve_5a = FoscPattern.indices(#[5,6], 8);
~sieve_5b = FoscPattern.indices(#[2,3,4], 5);
~sieve_5 = ~sieve_5a & ~sieve_5b;
~sieve_6a = FoscPattern.indices(#[1], 8);
~sieve_6b = FoscPattern.indices(#[2], 5);
~sieve_6 = ~sieve_6a & ~sieve_6b;
~sieve_7a = FoscPattern.indices(#[6], 8);
~sieve_7b = FoscPattern.indices(#[1], 5);
~sieve_7 = ~sieve_7a & ~sieve_7b;
~sieve = ~sieve_1 | ~sieve_2 | ~sieve_3 | ~sieve_4 | ~sieve_5 | ~sieve_6 | ~sieve_7;

~sieve.booleanVector(size: ~sieve.period);
------------------------------------------------------------------------------------------------------------ */
FoscPattern {
    classvar <nameToOperator;
    var <indices, period, <payload;
    var <patterns, <operator, <inverted=false;
    var <publishStorageFormat=true;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *initClass {
        nameToOperator = (
            'union': 'or',
            'sect': 'and',
            'symmetricDifference': 'xor'
        );
    }
    *new { |indices, period, payload|
        if (indices.notNil) {
            assert(indices.every { |each| each.isInteger }, thisMethod, 'indices', indices);
        };
        if (period.notNil) {
            assert(period.isInteger && { period > 0 }, thisMethod, 'period', period);
        };
        // if (inverted.notNil) { assert(inverted.isKindOf(Boolean)) };
        // if (patterns.notNil) {
        //     assert(patterns.isSequenceableCollection);
        //     assert(patterns.every { |each| each.isKindOf(this) });
        // };
        ^super.new.init(indices, period, payload);
    }
    init { |argIndices, argPeriod, argPayload|
        indices = argIndices;
        period = argPeriod;
        payload = argPayload;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *allIndices (index_all)

    Make pattern that matches all indices.

    p = FoscPattern.allIndices;
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *allIndices {
        ^FoscPattern(indices: #[0], period: 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *booleanVector

    Make pattern from boolean vector.

    p = FoscPattern.booleanVector(#[1,0,0,1,1,0]);
    p.booleanVector;
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *booleanVector { |vector|
        var period, indices;
        vector = vector.collect { |each| each.asBoolean };
        period = vector.size;
        indices = [];
        vector.do { |bool, i| if (bool) { indices = indices.add(i) } };
        ^FoscPattern(indices: indices, period: period);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *first (index_first)

    Make pattern that matches the first 'n' indices.

    p = FoscPattern.first(3);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *first { |n=1|
        var indices;
        assert(n.isInteger && { n >= 0 }, thisMethod, 'n', n);
        if (0 < n) { indices = (0..(n - 1)) };
        ^FoscPattern(indices: indices);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *indices (index)

    Make pattern that matches 'indices'.

    p = FoscPattern.indices(#[2]);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *indices { |indices, period|
        indices = indices ? [];
        assert(indices.every { |each| each.isInteger }, thisMethod, 'indices', indices);
        ^FoscPattern(indices: indices, period: period);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *last (index_last)

    Make pattern that matches the last 'n' indices.

    p = FoscPattern.last(3);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *last { |n=1|
        var indices;
        assert(n.isInteger && { n >= 0 }, thisMethod, 'n', n);
        if (0 < n) { indices = (-1..n.neg).reverse };
        ^FoscPattern(indices: indices);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *ratio

    p = FoscPattern.ratio(#[1,3,2]);
    p.booleanVector;
    p.invert.booleanVector;
    -------------------------------------------------------------------------------------------------------- */
    // *ratio { |ratio, repeat=false|
    //     var indices;
    //     assert(ratio.isSequenceableCollection, thisMethod, 'ratio', ratio);
    //     indices = ratio.deltas.drop(-1);
    //     ^FoscPattern(indices: indices);
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • indices

    Gets indices of pattern.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • inverted

    Is true when pattern is inverted.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • operator

    Gets operator of pattern.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • patterns

    Gets paterns of pattern.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • payload

    Gets payload of pattern.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • period

    Gets period of pattern.

    p = FoscPattern.indices(#[0], period: 3) | FoscPattern.indices(#[0], period: 4);
    p.period;


    p = FoscPattern.indices(#[0], period: 3);
    p.period;
    -------------------------------------------------------------------------------------------------------- */
    period {
        var periods;
        if (period.notNil) { ^period };
        if (patterns.notNil) {
            periods = patterns.collect { |each| each.period };
            if (periods.includes(nil).not) { ^periods.reduce('lcm') };
        };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • sum (weight)

    Gets sum of pattern.

    Sum defined equal to number of indices in pattern.

    Returns nonnegative integer.

    a = FoscPattern(#[0,2,3]);
    a.sum;
    -------------------------------------------------------------------------------------------------------- */
    sum {
        ^indices.size;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • |

    Union (logical OR) of two patterns. Synonymous with 'union'.

    a = "abcde";
    p = FoscPattern.first(2) union: FoscPattern.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    | { |pattern|
        ^this.union(pattern);
    }
    /* --------------------------------------------------------------------------------------------------------
    • &

    Intersection (logical AND) of two patterns. Synonymous with 'sect'.

    a = "abcde";
    p = FoscPattern.first(3) & FoscPattern.last(3);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    & { |pattern|
        ^this.sect(pattern);
    }
    /* --------------------------------------------------------------------------------------------------------
    • --

    Intersection (logical XOR) of two patterns. Synonymous with 'symmetricDifference'.

    a = "abcde";
    p = FoscPattern.first(2) -- FoscPattern.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    -- { |pattern|
        ^this.symmetricDifference(pattern);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    p = FoscPattern.indices(#[0,1,2], 5);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */ 
    /* --------------------------------------------------------------------------------------------------------
    • format

    p = FoscPattern.indices(#[0,1,2], 5);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert

    Inverts pattern in place.

    p = FoscPattern.indices(#[0,1,3], 4);
    p.booleanVector;

    p.invert;
    p.booleanVector;
    -------------------------------------------------------------------------------------------------------- */
    invert {
        this.instVarPut('inverted', inverted.not);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Intersection (logical AND) of two patterns.

    a = "abcde";
    p = FoscPattern.first(3) sect: FoscPattern.last(3);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    sect { |pattern|
        var new, selfPatterns, patterns;
        new = this.species.new();
        new.instVarPut('operator', thisMethod.name);
        if (this.prCanAppendToSelf(pattern, thisMethod.name)) {
            selfPatterns = this.patterns ?? [this];
            patterns = selfPatterns ++ [pattern];
            new.instVarPut('patterns', patterns);
        } {
            new.instVarPut('patterns', [this, pattern]);
        };
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • size

    p = FoscPattern.first(2);
    p.size;

    p = FoscPattern.first(2) | FoscPattern.last(2);
    p.size;
    -------------------------------------------------------------------------------------------------------- */
    size {
        var absoluteIndices, index, maximumIndex;
        if (period.notNil) { ^period };
        if (indices.notNil && { indices.notEmpty }) {
            absoluteIndices = [];
            indices.do { |index|
                if (0 <= index) {
                    absoluteIndices = absoluteIndices.add(index);
                } {
                    index = index.abs - 1;
                    absoluteIndices = absoluteIndices.add(index);
                };
            };
            maximumIndex = absoluteIndices.maxItem;
            ^(maximumIndex + 1);
        };
        ^0;
    }  
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[indices, period, payload];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscPattern.indices(#[0,1,2], 5);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.cs;
    }
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Intersection (logical XOR) of two patterns.

    a = "abcde";
    p = FoscPattern.first(2) symmetricDifference: FoscPattern.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    symmetricDifference { |pattern|
        var new, selfPatterns, patterns;
        new = this.species.new();
        new.instVarPut('operator', thisMethod.name);
        if (this.prCanAppendToSelf(pattern, thisMethod.name)) {
            selfPatterns = this.patterns ?? [this];
            patterns = selfPatterns ++ [pattern];
            new.instVarPut('patterns', patterns);
        } {
            new.instVarPut('patterns', [this, pattern]);
        };
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union (logical OR) of two patterns.

    a = "abcde";
    p = FoscPattern.first(2) union: FoscPattern.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    union { |pattern|
        var new, selfPatterns, patterns;
        new = this.species.new();
        new.instVarPut('operator', thisMethod.name);
        if (this.prCanAppendToSelf(pattern, thisMethod.name)) {
            selfPatterns = this.patterns ?? [this];
            patterns = selfPatterns ++ [pattern];
            new.instVarPut('patterns', patterns);
        } {
            new.instVarPut('patterns', [this, pattern]);
        };
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • booleanVector

    Gets boolean vector of pattern applied to input sequence with 'size'.


    • Example 1

    p = FoscPattern.indices(#[4,5,6,7]);
    
    p.booleanVector(4);
    
    p.booleanVector(8);

    p.booleanVector(16);

    'size' is set to size of pattern when no value is specified for 'size'.

    p.booleanVector;


    • Example 2

    Two part pattern with logical OR (union).

    p = FoscPattern.first(3) | FoscPattern.last(3);
    p.booleanVector(8);

    Vector of inverted pattern.

    p.invert;
    p.booleanVector(8);
    -------------------------------------------------------------------------------------------------------- */
    booleanVector { |size|
        var booleanVector, result;
        size = size ?? { this.size };
        booleanVector = [];
        size.do { |index|
            result = this.matchesIndex(index, size);
            booleanVector = booleanVector.add(result.binaryValue);
        };
        ^booleanVector;
    }
    /* --------------------------------------------------------------------------------------------------------
    • getMatchingItems

    Gets matching items from container, selection, or sequenceable collection.


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern.indices(#[4,5,6,7]);
    p.getMatchingItems(a);

    
    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern.indices(#[8,9], period: 10);
    p.getMatchingItems(a);    


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern.first(4);
    p.getMatchingItems(a); 


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern.last(3);
    p.getMatchingItems(a);  


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern.first(1) | FoscPattern.last(2);
    p.getMatchingItems(a);


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscPattern(#[0,-2,-1]);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    getMatchingItems { |object|
        var prototype, size, items, item;
        prototype = [FoscContainer, FoscSelection, SequenceableCollection];
        if (prototype.any { |type| object.isKindOf(type) }.not) {
            throw("%:%: argument must be a FoscContainer, FoscSelection or SequenceableCollection: %."
                .format(this.species, thisMethod.name, object));
        };
        size = object.size;
        items = [];
        size.do { |index|
            if (this.matchesIndex(index, size)) {
                item = object[index];
                items = items.add(item);
            };
        };
        ^items;
    }
    /* --------------------------------------------------------------------------------------------------------
    • matchesIndex

    Is true when pattern matches 'index' taken under 'totalLength'.

    p = FoscPattern.indices(#[0,1,7], period: 8);
    16.do { |i| i.post; Post.space; if (p.matchesIndex(i, 16)) { 'True'.post }; Post.nl };
    -------------------------------------------------------------------------------------------------------- */
    matchesIndex { |index, totalLength, rotation|
        var nonnegativeIndex, localPattern, localOperator, result, localResult;
        case
        { patterns.isNil || { patterns.isEmpty } } {
            assert(0 <= totalLength);
            if (0 <= index) {
                nonnegativeIndex = index;
            } {
                nonnegativeIndex = totalLength - index.abs;
            };
            if (indices.isNil || { indices.isEmpty }) { ^(false xor: inverted) };
            if (period.isNil) {
                indices.do { |index|
                    if (index < 0) { index = totalLength - index.abs };
                    if (index == nonnegativeIndex && { index < totalLength }) {
                        ^(true xor: inverted);
                    };
                };
            } {
                if (rotation.notNil) { nonnegativeIndex = nonnegativeIndex + rotation };
                nonnegativeIndex = nonnegativeIndex % period;
                indices.do { |index|
                    if (index < 0) {
                        index = totalLength - index.abs;
                        index = index % period;
                    };
                    if (index == nonnegativeIndex && { index < totalLength }) {
                        ^(true xor: inverted);
                    };
                    if (
                        (index % period) == nonnegativeIndex
                        && { (index % period) < totalLength }
                    ) {
                        ^(true xor: inverted);
                    };
                };
            };

            ^(false xor: inverted)
        }
        { patterns.size == 1 } {
            localPattern = patterns[0];
            result = localPattern.matchesIndex(index, totalLength, rotation: rotation);  
        }
        {
            localOperator = FoscPattern.nameToOperator[operator];
            localPattern = patterns[0];
            result = localPattern.matchesIndex(index, totalLength, rotation: rotation); 
            patterns[1..].do { |pattern|
                localResult = pattern.matchesIndex(index, totalLength, rotation: rotation);
                result = result.perform(localOperator, localResult);
            };
        };

        if (inverted) { result = result.not };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • reverse

    Reverses pattern.

    Returns new pattern.


    • Example 1

    p = FoscPattern(#[0,1,3], 4);
    p.booleanVector(8);

    p = p.reverse;
    p.booleanVector(8);


    • Example 2

    p = FoscPattern.first(3) | FoscPattern.last(1);
    p.booleanVector(6);

    p = p.reverse;
    p.booleanVector(6);
    -------------------------------------------------------------------------------------------------------- */
    reverse {
        var indices, new, patterns_;
        if (patterns.isNil || { patterns.isEmpty }) {
            indices = this.indices.collect { |index| index.neg - 1 };
            new = this.species.new(indices, period, payload);
            new.instVarPut('operator', operator);
            ^new;
        };
        patterns_ = patterns.collect { |each| each.reverse };
        new = this.species.new(this.indices, period, payload);
        new.instVarPut('operator', operator);
        new.instVarPut('patterns', patterns_);
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • rotate
    
    Rotates pattern by index 'n'.

    Returns new pattern.


    • Example 1

    p = FoscPattern(#[0,1,3], 4);
    p.booleanVector(8);

    p = p.rotate(1);
    p.booleanVector(8);

    p = p.rotate(-1);
    p.booleanVector(8);


    • Example 2

    p = FoscPattern.first(3) | FoscPattern.last(1);
    p.booleanVector(6);

    p = p.rotate(1);
    p.booleanVector(6);

    p = p.rotate(-1);
    p.booleanVector(6);
    -------------------------------------------------------------------------------------------------------- */
    rotate { |n|
        var indices, new, patterns_;
        if (patterns.isNil || { patterns.isEmpty }) {
            indices = this.indices.collect { |index| index + n };
            new = this.species.new(indices, period, payload);
            new.instVarPut('operator', operator);
            ^new;
        };
        patterns_ = patterns.collect { |each| each.rotate(n) };
        new = this.species.new(this.indices, period, payload);
        new.instVarPut('operator', operator);
        new.instVarPut('patterns', patterns_);
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCanAppendToSelf
    -------------------------------------------------------------------------------------------------------- */
    prCanAppendToSelf { |pattern, operator|
        if (pattern.isKindOf(this.species).not) { ^false };
        if (this.operator.isNil) { ^true };
        if (
            this.operator == operator
            && { pattern.operator.isNil || (pattern.operator == this.operator) }
        ) {
            ^true;
        };
        ^false;
    }
}
