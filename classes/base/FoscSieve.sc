/* ------------------------------------------------------------------------------------------------------------
• FoscSieve


• Example 1

Matches three indices in every eight.

p = FoscSieve(#[0,1,7], period: 8);
n = 16;
n.do { |i| Post << i << Char.tab << p.matchesIndex(i, n).binaryValue << nl };


Matches three indices in every sixteen.

p = FoscSieve(#[0,1,7], period: 16);
n = 16;
n.do { |i| Post << i << Char.tab << p.matchesIndex(i, n).binaryValue << nl };


Works with improper indices.

p = FoscSieve(#[16,17,23], period: 16);
n = 16;
n.do { |i| Post << i << Char.tab << p.matchesIndex(i, n).binaryValue << nl };


• Example 2

Make a simple rhythm from the intersection of sieves with different periodicities.

m = FoscSieve(#[0,1,7], 8) | FoscSieve(#[1,3], 5);
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 10, divisions: #[[1,1,1,1]], mask: m.mask);
b.show;


• Example 3

Sieve from the opening of Xenakis’s Psappha.

a = FoscSieve(#[0,1,7], 8) & FoscSieve(#[1,3], 5);
b = FoscSieve(#[0,1,2], 8) & FoscSieve(#[0], 5);
c = FoscSieve(#[3], 8);
d = FoscSieve(#[4], 8);
e = FoscSieve(#[5,6], 8) & FoscSieve(#[2,3,4], 5);
f = FoscSieve(#[1], 8) & FoscSieve(#[2], 5);
g = FoscSieve(#[6], 8) & FoscSieve(#[1], 5);
m = a | b | c | d | e | f | g;

m.booleanVector(size: m.period);

a = FoscMusicMaker();
b = a.(durations: 1/4 ! 10, divisions: #[[1,1,1,1]], mask: m.mask);
b.show;
------------------------------------------------------------------------------------------------------------ */
FoscSieve {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <nameToOperator;
    var <indices, period;
    var <sieves, <operator, <inverted=false;
    *initClass {
        nameToOperator = (
            'union': 'or',
            'sect': 'and',
            'symmetricDifference': 'xor'
        );
    }
    *new { |indices, period|
        ^super.new.init(indices, period);
    }
    init { |argIndices, argPeriod|
        indices = argIndices;
        period = argPeriod;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *allIndices (index_all)

    Make sieve that matches all indices.

    p = FoscSieve.allIndices;
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *allIndices {
        ^FoscSieve(indices: #[0], period: 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *booleanVector

    Make sieve from boolean vector.

    p = FoscSieve.booleanVector(#[1,0,0,1,1,0]);
    p.booleanVector;
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *booleanVector { |vector|
        var period, indices;
        
        vector = vector.collect { |each| each.asBoolean };
        period = vector.size;
        indices = [];
        vector.do { |bool, i| if (bool) { indices = indices.add(i) } };
        
        ^FoscSieve(indices: indices, period: period);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *first (index_first)

    Make sieve that matches the first 'n' indices.

    p = FoscSieve.first(3);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *first { |n=1|
        var indices;
        //assert(n.isInteger && { n >= 0 }, thisMethod, 'n', n);
        if (0 < n) { indices = (0..(n - 1)) };
        ^FoscSieve(indices: indices);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *last (index_last)

    Make sieve that matches the last 'n' indices.

    p = FoscSieve.last(3);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */
    *last { |n=1|
        var indices;
        //assert(n.isInteger && { n >= 0 }, thisMethod, 'n', n);
        if (0 < n) { indices = (-1..n.neg).reverse };
        ^FoscSieve(indices: indices);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • indices

    Gets indices of sieve.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • inverted

    Is true when sieve is inverted.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • operator

    Gets operator of sieve.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sieves

    Gets sieves of sieve.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • period

    Gets period of sieve.

    p = FoscSieve(#[0], period: 3) | FoscSieve(#[0], period: 4);
    p.period;


    p = FoscSieve(#[0], period: 3);
    p.period;
    -------------------------------------------------------------------------------------------------------- */
    period {
        var periods;
        
        if (period.notNil) { ^period };
        
        if (sieves.notNil) {
            periods = sieves.collect { |each| each.period };
            if (periods.includes(nil).not) { ^periods.reduce('lcm') };
        };
        
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • sum (weight)

    Gets sum of sieve.

    Sum defined equal to number of indices in sieve.

    Returns nonnegative integer.

    a = FoscSieve(#[0,2,3]);
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

    Union (logical OR) of two sieves. Synonymous with 'union'.

    a = "abcde";
    p = FoscSieve.first(2) union: FoscSieve.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    | { |sieve|
        ^this.union(sieve);
    }
    /* --------------------------------------------------------------------------------------------------------
    • &

    Intersection (logical AND) of two sieves. Synonymous with 'sect'.

    a = "abcde";
    p = FoscSieve.first(3) & FoscSieve.last(3);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    & { |sieve|
        ^this.sect(sieve);
    }
    /* --------------------------------------------------------------------------------------------------------
    • --

    Intersection (logical XOR) of two sieves. Synonymous with 'symmetricDifference'.

    a = "abcde";
    p = FoscSieve.first(2) -- FoscSieve.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    -- { |sieve|
        ^this.symmetricDifference(sieve);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    p = FoscSieve(#[0,1,2], 5);
    p.cs;
    -------------------------------------------------------------------------------------------------------- */ 
    /* --------------------------------------------------------------------------------------------------------
    • format

    p = FoscSieve(#[0,1,2], 5);
    p.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert

    Inverts sieve in place.

    p = FoscSieve(#[0,1,3], 4);
    p.booleanVector;

    p.invert;
    p.booleanVector;
    -------------------------------------------------------------------------------------------------------- */
    invert {
        this.instVarPut('inverted', inverted.not);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sect

    Intersection (logical AND) of two sieves.

    a = "abcde";
    p = FoscSieve.first(3) sect: FoscSieve.last(3);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    sect { |sieve|
        var new, lsieves, sieves;
        
        new = this.species.new;
        new.instVarPut('operator', thisMethod.name);
        
        if (this.prCanAppendToSelf(sieve, thisMethod.name)) {
            lsieves = this.sieves ?? [this];
            sieves = lsieves ++ [sieve];
            new.instVarPut('sieves', sieves);
        } {
            new.instVarPut('sieves', [this, sieve]);
        };
        
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • size

    p = FoscSieve.first(2);
    p.size;

    p = FoscSieve.first(2) | FoscSieve.last(2);
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
        ^[indices, period];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscSieve(#[0,1,2], 5);
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.cs;
    }
    /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference

    Intersection (logical XOR) of two sieves.

    a = "abcde";
    p = FoscSieve.first(2) symmetricDifference: FoscSieve.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    symmetricDifference { |sieve|
        var new, lsieves, sieves;
        
        new = this.species.new;
        new.instVarPut('operator', thisMethod.name);
        
        if (this.prCanAppendToSelf(sieve, thisMethod.name)) {
            lsieves = this.sieves ?? [this];
            sieves = lsieves ++ [sieve];
            new.instVarPut('sieves', sieves);
        } {
            new.instVarPut('sieves', [this, sieve]);
        };
        
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • union

    Union (logical OR) of two sieve.

    a = "abcde";
    p = FoscSieve.first(2) union: FoscSieve.last(2);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    union { |sieve|
        var new, lsieves, sieves;
        
        new = this.species.new;
        new.instVarPut('operator', thisMethod.name);
        
        if (this.prCanAppendToSelf(sieve, thisMethod.name)) {
            lsieves = this.sieves ?? [this];
            sieves = lsieves ++ [sieve];
            new.instVarPut('sieves', sieves);
        } {
            new.instVarPut('sieves', [this, sieve]);
        };
        
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • booleanVector

    Gets boolean vector of sieve applied to input sequence with 'size'.


    • Example 1

    p = FoscSieve(#[4,5,6,7]);
    
    p.booleanVector(4);
    
    p.booleanVector(8);

    p.booleanVector(16);

    'size' is set to size of sieve when no value is specified for 'size'.

    p.booleanVector;


    • Example 2

    Two part sieve with logical OR (union).

    p = FoscSieve.first(3) | FoscSieve.last(3);
    p.booleanVector(8);

    Vector of inverted sieve.

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
    p = FoscSieve(#[4,5,6,7]);
    p.getMatchingItems(a);

    
    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscSieve(#[8,9], period: 10);
    p.getMatchingItems(a);    


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscSieve.first(4);
    p.getMatchingItems(a); 


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscSieve.last(3);
    p.getMatchingItems(a);  


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscSieve.first(1) | FoscSieve.last(2);
    p.getMatchingItems(a);


    a = "abcdefghijklmnopqrstuvwxyz";
    p = FoscSieve(#[0,-2,-1]);
    p.getMatchingItems(a);
    -------------------------------------------------------------------------------------------------------- */
    getMatchingItems { |object|
        var type, size, items, item;
        
        type = [FoscContainer, FoscSelection, SequenceableCollection];
        
        if (type.any { |type| object.isKindOf(type) }.not) {
            ^throw("%:%: argument must be a FoscContainer, FoscSelection or SequenceableCollection: %."
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
    • mask

    Gets mask of sieve applied to input sequence with 'size'.


    • Example 1

    p = FoscSieve(#[4,5,6,8], period: 10);
    p.mask(20);


    • Example 2

    Apply to a rhythm.

    p = FoscSieve(#[4,5,6,8], period: 10);
    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: p.mask(20));
    b.show;


    • Example 3

    Apply to a rhythm with fusing.

    p = FoscSieve(#[4,5,6,8], period: 10);
    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: p.mask(20, fuse: true));
    b.show;


    • Example 4

    Sieve from the opening of Xenakis’s Psappha.

    a = FoscSieve(#[0,1,7], 8) & FoscSieve(#[1,3], 5);
    b = FoscSieve(#[0,1,2], 8) & FoscSieve(#[0], 5);
    c = FoscSieve(#[3], 8);
    d = FoscSieve(#[4], 8);
    e = FoscSieve(#[5,6], 8) & FoscSieve(#[2,3,4], 5);
    f = FoscSieve(#[1], 8) & FoscSieve(#[2], 5);
    g = FoscSieve(#[6], 8) & FoscSieve(#[1], 5);
    m = a | b | c | d | e | f | g;

    m.booleanVector(size: m.period);

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 10, divisions: #[[1,1,1,1]], mask: m.mask);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    mask { |size, fuse=false|
        var vector, result;

        size = size ?? { this.period };
        vector = this.booleanVector(size);

        if (fuse) {
            result = vector.separate { |a, b| (a == b && { a == 1 }) || { b - a == 1 } };
            result = result.collect { |each| each.size };
            if (this.indices[0] != 0) { result[0] = result[0].neg };
        } {
            result = vector.separate { |a, b| (a == b && { a == 1 }) || { a != b } };
            result = result.collect { |each| if (each[0] == 0) { each.size.neg } { each.size } };
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • matchesIndex

    Is true when sieve matches 'index' taken under 'totalLength'.

    p = FoscSieve(#[0,1,7], period: 8);
    16.do { |i| i.post; Post.space; p.matchesIndex(i, 16).post; Post.nl };
    -------------------------------------------------------------------------------------------------------- */
    matchesIndex { |index, totalLength, rotation|
        var nonnegativeIndex, localPattern, localOperator, result, localResult;
        
        case
        { sieves.isNil || { sieves.isEmpty } } {
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
                    if (index == nonnegativeIndex && { index < totalLength }) { ^(true xor: inverted) };
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
        { sieves.size == 1 } {
            localPattern = sieves[0];
            result = localPattern.matchesIndex(index, totalLength, rotation: rotation);  
        }
        {
            localOperator = FoscSieve.nameToOperator[operator];
            localPattern = sieves[0];
            result = localPattern.matchesIndex(index, totalLength, rotation: rotation); 
            
            sieves[1..].do { |sieve|
                localResult = sieve.matchesIndex(index, totalLength, rotation: rotation);
                result = result.perform(localOperator, localResult);
            };
        };

        if (inverted) { result = result.not };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • reverse

    Reverses sieve.

    Returns new sieve.


    • Example 1

    p = FoscSieve(#[0,1,3], 4);
    p.booleanVector(8);

    p = p.reverse;
    p.booleanVector(8);


    • Example 2

    p = FoscSieve.first(3) | FoscSieve.last(1);
    p.booleanVector(6);

    p = p.reverse;
    p.booleanVector(6);
    -------------------------------------------------------------------------------------------------------- */
    reverse {
        var indices, new, lsieves;
        
        if (sieves.isNil || { sieves.isEmpty }) {
            indices = this.indices.collect { |index| index.neg - 1 };
            new = this.species.new(indices, period);
            new.instVarPut('operator', operator);
            ^new;
        };
        
        lsieves = sieves.collect { |each| each.reverse };
        new = this.species.new(this, period);
        new.instVarPut('operator', operator);
        new.instVarPut('sieves', lsieves);
        
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • rotate
    
    Rotates sieve by index 'n'.

    Returns new sieve.


    • Example 1

    p = FoscSieve(#[0,1,3], 4);
    p.booleanVector(8);

    p = p.rotate(1);
    p.booleanVector(8);

    p = p.rotate(-1);
    p.booleanVector(8);


    • Example 2

    p = FoscSieve.first(3) | FoscSieve.last(1);
    p.booleanVector(6);

    p = p.rotate(1);
    p.booleanVector(6);

    p = p.rotate(-1);
    p.booleanVector(6);
    -------------------------------------------------------------------------------------------------------- */
    rotate { |n|
        var indices, new, lsieves;
        
        if (sieves.isNil || { sieves.isEmpty }) {
            indices = this.indices.collect { |index| index + n };
            new = this.species.new(indices, period);
            new.instVarPut('operator', operator);
            ^new;
        };
        
        lsieves = sieves.collect { |each| each.rotate(n) };
        new = this.species.new(this, period);
        new.instVarPut('operator', operator);
        new.instVarPut('sieves', lsieves);
        
        ^new;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCanAppendToSelf
    -------------------------------------------------------------------------------------------------------- */
    prCanAppendToSelf { |sieve, operator|
        if (sieve.isKindOf(this.species).not) { ^false };
        if (this.operator.isNil) { ^true };
        
        if (
            this.operator == operator
            && { sieve.operator.isNil || (sieve.operator == this.operator) }
        ) {
            ^true;
        };
        
        ^false;
    }
}
