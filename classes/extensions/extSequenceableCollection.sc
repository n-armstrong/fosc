/* ------------------------------------------------------------------------------------------------------------
• SequenceableCollection
------------------------------------------------------------------------------------------------------------ */
+ SequenceableCollection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asFormattedString

    !!!TODO: recursively handle items with rank > 1

    a = "    ratio  freq      dB\n";
    b = #[1, 1, 2, 3.98783].asFormattedString(#[3,6,6,8],#[0,2,1,3]);
    (a ++ b).postln; "";
    -------------------------------------------------------------------------------------------------------- */
    asFormattedString { |columnWidth=6, precision=2|
        var result, lcolumnWidth, lprecision, characteristic, mantissa, str;
        
        if (columnWidth.isSequenceableCollection.not) { columnWidth = [columnWidth] };
        if (precision.isSequenceableCollection.not) { precision = [precision] };

        result = "";
        
        this.do { |item, i|
            lcolumnWidth = columnWidth.wrapAt(i);

            if (item.isNumber) {
                lprecision = precision.wrapAt(i);
                str = characteristic = item.asInteger.asString;
                mantissa = item.frac.round(10 ** lprecision.neg);
                mantissa = mantissa.asString.drop(2);
                mantissa = mantissa.padRight(lprecision, "0");
                if (lprecision != 0) { str = "%.%".format(characteristic, mantissa) };
                str = str.padLeft(lcolumnWidth, " ");

            } {
                str = item.asString;
                str = str.padLeft(lcolumnWidth - str.size, " ");
            };

            result = result ++ str;
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • atN

    x = [[1, 2], 3, [4, 5, 6]];
    x.atN(0, 1);
    x.atN(2, 0);
    x.atN(2, 2);
    x.atN(2, 3);
    -------------------------------------------------------------------------------------------------------- */
    atN { |...indices|
        var result;
        
        result = this.copy;
        indices.do { |index| result = result[index] };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • bisect

    Locate the insertion point for val in array to maintain sorted order.

    a = #[1,2,3,4,7,8,9,10];
    a.bisect(5);

    a = #[10,9,8,1];
    a.bisect(5);
    -------------------------------------------------------------------------------------------------------- */
    bisect { |val|
        var result;
        
        if (this.includes(val).not) {
            result = this.copy.add(val).sort;
            result = result.indexOf(val);
            ^result;
        } {
            ^(this.indexOf(val) + 1);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • extendToAbsSum

    [1, 2, 3].extendToAbsSum(10);
    [1, 2, -3].extendToAbsSum(10);
    [1, 2, 3, 99].extendToAbsSum(10);   // truncate if needed
    -------------------------------------------------------------------------------------------------------- */
    extendToAbsSum { |sum|
        var result, difference;
        
        if (sum <= 0) { ^throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
        result = this.copy;
        if (result.abs.sum == sum) { ^this };
        difference = sum - (result.abs.sum);
        result = result.add(difference);
        if (result.abs.sum > sum) { result = result.truncateToAbsSum(sum) };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • getItem

    Identical to __getitem__ in python.

    (0..5).prGetItem((2..4));
    (0..5).prGetItem(2);
    (0..5).prGetItem((4..9));
    (0..5).prGetItem((9..11));
    (0..5).prGetItem(9);      // nil: out of range when int rather than slice
    -------------------------------------------------------------------------------------------------------- */
    getItem { |indices|
        if (indices.isSequenceableCollection) { indices = indices.sort };
        if (indices.isInteger) { ^this[indices] };
        if (indices.last < this.size) { ^this.atAll(indices) };
        if (indices.last > this.size && { indices.first < this.size }) { ^this[indices.first..] };
        if (indices.first >= this.size) { ^[] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • intervals
    -------------------------------------------------------------------------------------------------------- */
    intervals {
        var result;
        
        result = [];
        this.doAdjacentPairs { |a, b| result = result.add(b - a) };
        
        if (result.any { |each| each.isKindOf(FoscOffset) }) {
            result = result.collect { |each| FoscDuration(each) };
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • offsets
    -------------------------------------------------------------------------------------------------------- */
    offsets {
        var result;
        
        result = ([0] ++ this.integrate);
        
        if (result.any { |each| each.isKindOf(FoscDuration) }) {
            result = result.collect { |each| FoscOffset(each) };
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • orderN

    x = [[3, 2], [3, 1], [1, 7], [2, 0]];
    x.orderN;
    x[x.orderN]; // sorted
    -------------------------------------------------------------------------------------------------------- */
    orderN {
        var sorted, order;
        
        sorted = this.copy.sortN;
        order = sorted.collect { |each| this.indexOf(each) };
        
        ^order;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionBySizes

    Partitions receiver by sizes.

    Returns a nested collection.


    • Example 1

    (0..16).partitionBySizes([3]);


    • Example 2

    (0..16).partitionBySizes([3], overhang: true);

    (0..16).clump(3);


    • Example 3
    
    (0..16).partitionBySizes([3], isCyclic: true);


    • Example 4
    
    (0..16).partitionBySizes([3], isCyclic: true, overhang: true);
    -------------------------------------------------------------------------------------------------------- */
    partitionBySizes { |sizes, isCyclic=false, overhang=false|
        var result, count, i=0, start=0, stop, part;
        
        assert(
            sizes.every { |expr| expr.isInteger && { expr >= 0 } },
            "%:%: sizes must be non-negative integers: %.".format(this.species, thisMethod.name, sizes)
        );
        
        result = [];
        
        block { |break|
            inf.do {
                count = if (isCyclic) { sizes.wrapAt(i) } { sizes[i] };
                stop = start + count;
                part = this[start..(stop - 1)];
                
                if (this.lastIndex < stop) {
                    break.value;
                };
                
                result = result.add(part);
                start = stop;
                i = i + 1;
                
                if (isCyclic.not && { sizes.lastIndex < i }) {
                    part = this[start..];
                    break.value;
                };
            };
        };
        
        if (part.notEmpty) {
            case 
            { overhang } { result = result.add(part) }
            { part.size == count } { result = result.add(part) };
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionByRatio

    Partitions receiver into nearest integer-sized parts by ratio.

    Returns a nested collection.


    • Example 1

    (0..15).partitionByRatio(#[1,1]);


    • Example 2

    (0..15).partitionByRatio(#[1,2,3]);
    -------------------------------------------------------------------------------------------------------- */
    partitionByRatio { |ratio|
        var sizes, parts;
        
        sizes = this.size.partitionByRatio(ratio);
        parts = this.partitionBySizes(sizes);
        
        ^parts;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionBySums

    Partitions collection by sums.

    Returns new collection.
    

    l = #[10,-10,10,-10];
    l.partitionBySums(#[3,15,3], overhang: true).printAll;

    l = #[10,-10,10,-10];
    l.partitionBySums(#[3,15,3], overhang: false).printAll;

    l = #[10,-10,10,-10];
    l.partitionBySums(#[3,15,3], isCyclic: true, overhang: true).printAll;


    l = #[10,-10,10,-10];
    l.partitionBySums(#[3,15,3], isCyclic: false, overhang: true).printAll;

    l = #[10,10,10, 10].collect { |each| FoscDuration(each) };
    m = l.partitionBySums(#[3,15,3], isCyclic: false, overhang: true);
    m.do { |each| each.collect { |elem| elem.str }.postln };


    l = #[10,10,10,10].collect { |each| FoscDuration(each) };
    n = #[3,15,3].collect { |each| FoscNonreducedFraction(each) };
    m = l.partitionBySums(n, isCyclic: false, overhang: true);
    m.do { |each| each.collect { |elem| elem.str }.postln };
    -------------------------------------------------------------------------------------------------------- */
    partitionBySums { |sums, isCyclic=false, overhang=false|
        var result, currentIndex, currentPiece, currentPieceSum, overage, currentLastElement, needed;
        var lastPiece;
        
        result = [];
        currentIndex = 0;
        currentPiece = [];
        
        if (sums.sum > this.abs.sum) {
            sums = sums.truncateToAbsSum(this.abs.sum);
        };
        
        if (isCyclic) {
            sums = sums.repeatToAbsSum(this.abs.sum, 'less');
        };
        
        sums.do { |sum|
            currentPieceSum = currentPiece.abs.sum;
            
            while { currentPieceSum < sum } {
                currentPiece = currentPiece.add(this[currentIndex]);
                currentIndex = currentIndex + 1;
                currentPieceSum = currentPiece.abs.sum;
            };
            
            case
            { currentPieceSum == sum } {
                currentPiece = this.species.newFrom(currentPiece);
                result = result.add(currentPiece);
                currentPiece = [];
            }
            { sum < currentPieceSum } {
                overage = currentPieceSum - sum;
                currentLastElement = currentPiece.pop;
                needed = currentLastElement.abs - overage;
                needed = needed * currentLastElement.sign;
                currentPiece = currentPiece.add(needed);
                currentPiece = this.species.newFrom(currentPiece);
                result = result.add(currentPiece);
                overage = overage * currentLastElement.sign;
                currentPiece = [overage];
            };
        };
        
        if (overhang) {
            lastPiece = currentPiece;
            lastPiece = lastPiece.addAll(this[currentIndex..]);
            
            if (lastPiece.notEmpty) {
                lastPiece = this.species.newFrom(lastPiece);
                result = result.add(lastPiece);
            };
        };
        
        ^this.species.newFrom(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    split { |sums, isCyclic=false, overhang=false|
        ^this.partitionBySums(sums, isCyclic, overhang);
    }
    /* --------------------------------------------------------------------------------------------------------
	• reduceFraction


	• Example 1
    
    [#28,24].reduceFraction;


    • Example 2

	#[28,25].reduceFraction;
	-------------------------------------------------------------------------------------------------------- */
	reduceFraction {
		var msg, numerator, denominator;
		
        if (this.size != 2) {
            msg = "%:reduceFraction: receiver size must be 2: %.".format(this.species, this.size);
            ^throw(msg);
        };
		
        this.do { |item|
            if (item === inf || { item === -inf }) { ^this };
			
            if (item.isKindOf(Integer).not) {
				^throw("%:reduceFraction: items in receiver must be integers: %."
                    .format(this.species, item));
			};
		};
		
        # numerator, denominator = (this / this.reduce('gcd')).asInteger; 
		
        ^[numerator, denominator];
	}
    /* --------------------------------------------------------------------------------------------------------
    • removeDuplicates


    • Example 1

    a = [1, 1, 2, 2, 3, 3, 4, 4, 5, 5];
    a.removeDuplicates.postln;
    -------------------------------------------------------------------------------------------------------- */
	removeDuplicates {
        var result;
        
        result = this.species.new;
        this.do { |item| if (result.indexOfEqual(item).isNil) { result = result.add(item) } };
        
        ^result
    }
    /* --------------------------------------------------------------------------------------------------------
    • repeatToAbsSum

    Repeats collection to absolute sum.

    Returns new collection.

    
    [1, 2, 3].repeatToAbsSum(15);
    [1, 2, -3].repeatToAbsSum(15);
    [1, 2, 3].repeatToAbsSum(14.5);

    a = [[3, 16], [-2, 16]].collect { |each| FoscNonreducedFraction(each) };
    b = a.repeatToAbsSum(FoscNonreducedFraction(5, 4));
    b.do { |each| each.pair.postln };

    a = [[3, 16], [2, 16]].collect { |each| FoscDuration(each) };
    b = a.repeatToAbsSum(FoscDuration(5, 4));
    b.do { |each| each.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
    repeatToAbsSum { |sum, allowTotal='exact'|
        var sequenceSum, completeRepetitions, items, overage, elementSum, candidateOverage;
        var absoluteAmountToKeep, signedAmountToKeep, i;
        
        if (sum <= 0) { ^throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
        
        switch(allowTotal,
            'exact', {  
                sequenceSum = this.abs.sum;
                completeRepetitions = (sum.asFloat / sequenceSum.asFloat).ceil.asInteger;
                items = this.copy;
                items = (items ! completeRepetitions).flat;
                overage = (completeRepetitions * sequenceSum) - sum;
                
                block { |break|
                    items.reverse.do { |item|
                        if (overage > 0) {
                            elementSum = item.abs;
                            candidateOverage = overage - elementSum;
                            
                            if (candidateOverage >= 0) {
                                overage = candidateOverage;
                                items.pop;
                            } {
                                absoluteAmountToKeep = elementSum - overage;
                                signedAmountToKeep = absoluteAmountToKeep * item.sign;
                                items.pop;
                                items = items.add(signedAmountToKeep);
                                break.value;
                            };
                        } {
                            break.value;
                        }
                    };
                };
            },
            'less', {
                items = [this[0]];
                i = 1;
                
                while { items.abs.sum < sum } {
                    items = items.add(this[i % this.size]);
                    i = i + 1;
                };
                
                if (sum < items.abs.sum) {
                    items = items.drop(-1);
                    ^this.species.newFrom(items);
                };
            },
            'more', {
                items = [this[0]];
                i = 1;
                
                while { items.abs.sum < sum } {
                    items = items.add(this[i % this.size]);
                    i = i + 1;
                    ^this.species.newFrom(items);
                };

            }
        );

        ^this.species.newFrom(items);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sortN

    Sort an n-dimensional collection in ascending or descending order.


    • Example 1

    a = { [1, 9.rand, 9.rand] } ! 10;
    a.sortN.printAll;


    • Example 2

    a = { "abracadabra".scramble } ! 10;
    a.sortN.printAll;


    • Example 3

    a = { [9.rand, 9.rand, 9.rand] } ! 10;
    a.sortN(reverse: true).printAll;


    • Example 4

    Sort from 'startIndex'. If 'startIndex' is 1, then 0th element is ignored in search function.

    a = { [9.rand, 9.rand, 9.rand] } ! 10;
    a.sortN(startIndex: 1).printAll;
    -------------------------------------------------------------------------------------------------------- */
    sortN { |startIndex=0, reverse=false|
        var func, i, result, minSize, operator;
        
        if (this.rank == 1) { ^this.sort };

        operator = if (reverse) { '>' } { '<' };
        
        func = { |a, b|
            i = startIndex;
            result = true;
            minSize = min(a.size, b.size);
            
            while { i <= (minSize - 1) } {
                if (a[i].perform(operator, b[i])) {
                    i = minSize;
                } {
                    if (b[i].perform(operator, a[i])) {
                        i = minSize;
                        result = false
                    } {
                        i = i + 1;
                    };
                };
            };

            result;
        };

        ^this.sort(func);
    }
    /* --------------------------------------------------------------------------------------------------------
	• truncateToAbsSum

	Truncates collection to absolute sum of values.

	Returns new collection.

	l = [-1, 2, -3, 4, -5, 6, -7, 8, -9, 10];
	(1..11).do { |sum| sum.post; " ".post; l.truncateToAbsSum(sum).postln };
	-------------------------------------------------------------------------------------------------------- */
	truncateToAbsSum { |sum|
		var items, total=0, sign, trimmedPart;
		
        if (sum <= 0) { ^throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
		items = [];
		
        block { |break|
			if (sum > 0) {
				this.do { |item|
					total = total + item.abs;
					
                    if (total < sum) {
						items = items.add(item);
					} {
						sign = item.sign;
						trimmedPart = sum - items.abs.sum;
						trimmedPart = trimmedPart * sign;
						items = items.add(trimmedPart);
						break.value;
					};
				};
			};
		};

		^this.species.newFrom(items);
	}
	/* --------------------------------------------------------------------------------------------------------
	• truncateToSum

	Truncates collection to sum of values.

	Returns new collection.
	
	l = [-1, 2, -3, 4, -5, 6, -7, 8, -9, 10];
	(1..11).do { |sum| sum.post; " ".post; l.truncateToSum(sum).postln };

	a = (1..10).collect { |each| FoscDuration(each, 16) };
  	b = a.truncateToSum(FoscDuration(7, 4));
  	b.do { |each| each.pair.postln };
	-------------------------------------------------------------------------------------------------------- */
	truncateToSum { |sum|
		var items, total=0;
		
        if (sum <= 0) { ^throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
		items = [];
		
        block { |break|
			if (sum > 0) {
				this.do { |item|
					total = total + item;
					
                    if (total < sum) {
						items = items.add(item);
					} {
						items = items.add(sum - items.sum);
						break.value;
					};
				};
			};
		};

		^this.species.newFrom(items);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetItem

    Identical to __getitem__ in python.

    (0..5).prGetItem((2..4));
    (0..5).prGetItem(2);
    (0..5).prGetItem((4..9));
    (0..5).prGetItem((9..11));
    (0..5).prGetItem(9);
    -------------------------------------------------------------------------------------------------------- */
    prGetItem { |indices|
        var result;
        if (indices.isSequenceableCollection) { indices = indices.sort };
        if (indices.isInteger) { ^this[indices] };
        if (indices.last < this.size) { ^this.atAll(indices) };
        if (indices.last > this.size && { indices.first < this.size }) { ^this[indices.first..] };
        if (indices.first >= this.size) { ^[] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateTopDown

    Recursively iterate over score components or a collection.

    a = FoscMeasure([2, 4], [FoscNote(60, [2, 4])]);
    b = FoscMeasure([4, 4], [
        FoscNote(60, [1, 32]),
        FoscNote(62, [7, 8]),
        FoscNote(62, [1, 16]),
        FoscNote(64, [1, 32])
    ]);
    c = FoscMeasure([2, 4], [FoscNote(64, [2, 4])]);
    x = FoscStaff([a, b, c]);
    y = FoscScore([x]);
    z = [y];

    y.prIterateTopDown.do { |each| each.prGetParentage.depth.do { Post.tab }; each.postln };
    
    z.prIterateTopDown.do { |each| each.prGetParentage.depth.do { Post.tab }; each.postln };
    
    FoscSelection(z).prIterateTopDown.do { |each| each.prGetParentage.depth.do { Post.tab }; each.postln };
    -------------------------------------------------------------------------------------------------------- */
    prIterateTopDown {
        var routine, recurse;
        
        routine = Routine {
            recurse = { |node| 
                case
                { node.isSequenceableCollection } {
                    node.do { |each| recurse.(each) };
                }
                { node.isKindOf(FoscContainer) } {
                    node.prIterateTopDown.do { |each| each.yield };
                }
                { node.isKindOf(FoscSelection) } {
                    node.prIterateTopDown.do { |each| each.yield };
                }
                { node.isKindOf(FoscLeaf) } {
                    node.yield;
                }
            };

            recurse.(this);
        };

        ^routine;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetItem

    Replicates the Python '__setitem__' special method. Returns a new list (Python version operates in place).

    For easy porting of code from Python only. Not for public use.


    (0..5).prSetItem((0..1), #[a, b, c]);       // YES

    (0..5).prSetItem((2..4), #[a, b, c]);       // YES
    (0..5).prSetItem((2..4), #[[a, b, c]]);     // YES

    (0..5).prSetItem(1, #[a, b, c]);            // YES
    (0..5).prSetItem(99, #[a, b, c]);           // YES (index out of range error)

    (0..5).prSetItem((0..0), #[a, b, c]);       // YES (equivalent to 'insert')
    (0..5).prSetItem((1..1), #[a, b, c]);       // YES (equivalent to 'insert')
    (0..5).prSetItem((1..1), #[[a, b, c]]);     // YES (equivalent to 'insert')
    (0..5).prSetItem((99..99), #[a, b, c]);     // YES (equivalent to 'insert')
        

    (0..5).prSetItem((4..99), #[a, b, c]);      // YES
    (0..5).prSetItem((99..101), #[a, b, c]);    // YES
    (0..5).prSetItem((99..101), #[[a, b, c]]);  // YES
    
    (0..5).prSetItem((1..4), #[a, b, c]);       // YES
    (0..5).prSetItem((1..4), #[[a, b, c]]);     // YES

    (0..5).prSetItem((2..99), #[a, b, c]);      // YES


    (0..5).prSetItem((0..1), #[a, b, c]);       // YES
    (0..5).prSetItem((2..4), #[a, b, c]);       // YES
    (0..5).prSetItem((2..7), #[a, b, c]);       // YES
    (0..5).prSetItem((7..9), #[a, b, c]);       // YES
    -------------------------------------------------------------------------------------------------------- */
    prSetItem { |index, contents|
        var result, sliceA, sliceB, rmvIndices;
        
        result = this.copy;
        
        case
        { index.isInteger } {
            if (index < this.size) {
                result[index] = contents;
                ^result;
            } {
                ^throw("%:%: index out of range: %.".format(this.species, thisMethod.name, index))
            };
        }
        { index.isSequenceableCollection } {
            index = index.sort;
            
            case
            { index.first >= this.size } {
                result = result.addAll(contents);
                ^result;
            }
            { index.first == index.last } {
                sliceA = result[(index.first - 1).min(0)..(index.first - 1)];
                sliceB = result[index.first..];
                result = sliceA ++ contents ++ sliceB;
                ^result;
            }
            {
                index.drop(-1).reverseDo { |i|
                    if (i.inclusivelyBetween(0, this.lastIndex)) { result.removeAt(i) };
                };
                
                if (index.first <= 0) {
                    sliceA = this.species.new;
                } {
                    sliceA = result[0..(index.first - 1)];
                };
                
                sliceB = result[index.first..];
                result = sliceA ++ contents ++ sliceB;
                ^result;
            };
        }
        {
            ^throw("%:%: bad value for 'index': %.".format(this.species, thisMethod.name, index));
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: FOR DEPRECATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • iterate

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    iterate {
        ^FoscIteration(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • mutate

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    mutate {
        ^FoscMutation(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLeaves

    -- mirror selection and iteration methods in Fosc

    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    doLeaves { |function, type, pitched, graceNotes=false|
        var iterator;
        
        Fosc.prCheckIsIterable(this, thisMethod);
        
        iterator = FoscIteration(this).leaves(type: type, pitched: pitched, graceNotes: graceNotes);

        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectComponents
   
     -- mirror selection and iteration methods in Fosc
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    selectComponents { |type, exclude, graceNotes=false, reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);  
       
        ^FoscSelection(this).components(
            type: type,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLeaves

    -- mirror selection and iteration methods in Fosc
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    selectLeaves { |type, exclude, graceNotes=false, pitched, reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        
        ^FoscSelection(this).leaves(
            type: type,
            exclude: exclude,
            graceNotes: graceNotes,
            pitched: pitched,
            reverse: reverse
        );
    
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLogicalTies

     -- mirror selection and iteration methods in Fosc
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    selectLogicalTies { |exclude, graceNotes=false, nontrivial, pitched, reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);   
        
        ^FoscSelection(this).logicalTies(
            exclude: exclude,
            graceNotes: graceNotes,
            nontrivial: nontrivial,
            pitched: pitched,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectRuns

    -- mirror selection and iteration methods in Fosc
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection

    !!!TODO: deprecate
    -------------------------------------------------------------------------------------------------------- */
    selectRuns { |exclude|
        Fosc.prCheckIsIterable(this, thisMethod);
        
        ^FoscSelection(this).runs(exclude: exclude);
    }
}
