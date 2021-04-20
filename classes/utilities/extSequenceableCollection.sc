/* ------------------------------------------------------------------------------------------------------------
• SequenceableCollection
------------------------------------------------------------------------------------------------------------ */
+ SequenceableCollection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *geom2

    Returns a geometric series.

    size: the size of the series
    start: the starting value of the series
    end: the ending value of the series


    • Example 1

    A 12ET chromatic scale.

    a = Array.geom2(13, 1, 2).round(0.001).printAll;

    Array.interpolation(13, 0, 1).linexp(0, 1, 1, 2).round(0.001).printAll;


    • Example 2

    A 12ET chromatic scale in reverse.

    a = Array.geom2(13, 2, 1).round(0.001).printAll;
    -------------------------------------------------------------------------------------------------------- */
    *geom2 { |size=10, start=1, end=2|
        var m, result;
        if (end >= start) { m = end / start } { m = start / end };
        result = this.geom(size, 1, m ** (1 / (size - 1)));
        result = result.linlin(1, m, start, end);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *halfCos

    Create a S-shaped half-cosine curve.


    • Example 1

    a = Array.halfCos(size: 100);
    a.plot;
    -------------------------------------------------------------------------------------------------------- */
    *halfCos { |size=10, start=0, end=1|
        var series, result;
        series = Array.interpolation(size, -pi/2, pi/2);
        result = sin(series);
        result = result.linlin(result.minItem, result.maxItem, start, end);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *sigmoid

    Create a S-shaped curve using the hyperbolic tangent function.

    See https://en.wikipedia.org/wiki/Sigmoid_function.


    • Example 1

    a = Array.sigmoid(size: 100, curve: 1);
    a.plot;


    • Example 2

    a = Array.sigmoid(size: 100, curve: 2);
    a.plot;
    -------------------------------------------------------------------------------------------------------- */
    *sigmoid { |size=10, start=0, end=1, curve=1|
        var series, result;
        series = Array.interpolation(size, curve.neg, curve);
        result = tanh(series);
        result = result.linlin(result.minItem, result.maxItem, start, end);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *sigmoid2

    !!!TODO: rename, or work into 'sigmoid' definition.

    Create an inverse S-shaped curve using the hyperbolic sine function.


    • Example 1

    a = Array.sigmoid2(size: 100, curve: 1);
    a.plot;


    • Example 2

    a = Array.sigmoid2(size: 100, curve: 4);
    a.plot;
    -------------------------------------------------------------------------------------------------------- */
    *sigmoid2 { |size=10, start=0, end=1, curve=1|
        var series, result;
        series = Array.interpolation(size, curve.neg, curve);
        result = sinh(series);
        result = result.linlin(result.minItem, result.maxItem, start, end);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *smoothStep

    Create a S-shaped curve using the smooth step function.


    • Example 1

    a = Array.smoothStep(size: 100);
    a.plot;
    -------------------------------------------------------------------------------------------------------- */
    *smoothStep { |size=10, start=0, end=1|
        var series, result;
        series = Array.interpolation(size, 0, 1);
        result = series.collect { |x| x * x * (3 - (2 * x)) };
        result = result.linlin(result.minItem, result.maxItem, start, end);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *smootherStep

    • TODO: rename 'smoothStep2' for consistency

    Create a S-shaped curve using the smoother step function.


    • Example 1

    a = Array.smootherStep(size: 100);
    a.plot;
    -------------------------------------------------------------------------------------------------------- */
    *smootherStep { |size=10, start=0, end=1|
        var series, result;
        series = Array.interpolation(size, 0, 1);
        result = series.collect { |x| x * x * x * (x * ((x * 6) - 15) + 10) };
        result = result.linlin(result.minItem, result.maxItem, start, end);
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    a = [1, 2, 3, 4, 7, 8, 9, 10];
    a.bisect(5);

    a = [10, 9, 8, 1];
    a.bisect(5);
    -------------------------------------------------------------------------------------------------------- */
    bisect { |val|
        var result;
        if (this.includes(val).not) {
            result = this.add(val).sort;
            result = result.indexOf(val);
            ^result;
        } {
            ^(this.indexOf(val) + 1);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • incise

    x = #[4,3,4,2];
    x.incise(0)
    -------------------------------------------------------------------------------------------------------- */
    incise { |index=0, n=1|
        var result;
        result = [];
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
    • iterate
    -------------------------------------------------------------------------------------------------------- */
    //!!!TODO: deprecate ??
    iterate {
        ^FoscIteration(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • mask

    a = (1..10);
    a.mask([1, -1, 1, 1, -1, -1], isCyclic: false);
    a.mask([1, -1, 1, 1, -1, -1], isCyclic: true);

    a = (1..10);
    a.mask([-1, 1], isCyclic: true);

    a = (1..10);
    a.mask([false, true], isCyclic: true);
    -------------------------------------------------------------------------------------------------------- */
    mask { |pattern, isCyclic=false|
        var result, val;
        result = this.species.newClear(this.size);
        pattern = pattern.collect { |each| each.binaryValue };
        pattern = pattern.collect { |each| if (each == 0) { each = -1 } { each } };
        this.do { |item, i|
            if (isCyclic) {
                result[i] = item * pattern.wrapAt(i);
            } { 
                if (pattern[i].notNil) {
                    result[i] = item * pattern[i];
                } {
                    result[i] = item;
                };
            };
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
    • mutate
    -------------------------------------------------------------------------------------------------------- */
    mutate {
        ^FoscMutation(this);
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
        var result;
        if (indices.isSequenceableCollection) { indices = indices.sort };
        if (indices.isInteger) { ^this[indices] };
        if (indices.last < this.size) { ^this.atAll(indices) };
        if (indices.last > this.size && { indices.first < this.size }) { ^this[indices.first..] };
        if (indices.first >= this.size) { ^[] };
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

    (0..15).partitionByRatio(#[1, 1]);


    • Example 2

    (0..15).partitionByRatio(#[1, 2, 3]);
    -------------------------------------------------------------------------------------------------------- */
    partitionByRatio { |ratio|
        var sizes, parts;
        sizes = this.size.partitionByRatio(ratio);
        parts = this.partitionBySizes(sizes);
        ^parts;
    }
    /* --------------------------------------------------------------------------------------------------------
	• reduceFraction


	• Example 1
    
    [28, 24].reduceFraction;


    • Example 2

	[28, 25].reduceFraction;
	-------------------------------------------------------------------------------------------------------- */
	reduceFraction {
		var numerator, denominator;
		if (this.size != 2) { throw("%:reduceFraction: receiver size must be 2: %."
            .format(this.species, this.size)) };
		this.do { |item|
            if (item === inf || { item === -inf }) { ^this };
			if (item.isKindOf(Integer).not) {
				throw("%:reduceFraction: items in receiver must be integers: %."
                    .format(this.species, item));
			};
		};
		# numerator, denominator = (this / this.reduce(\gcd)).asInteger; 
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
  		if (sum <= 0) { throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
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

	  	});
	  	^this.species.newFrom(items);
	}
    /* --------------------------------------------------------------------------------------------------------
    • sortN

    Sort an n-dimensional collection in ascending or descending order.


    • Example 1

    a = { [9.rand, 9.rand, 9.rand] } ! 10;
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
	• split

	Splits collection by sums.

	Returns new collection.
	

	l = #[10,-10,10,-10];
	l.split([3, 15, 2], overhang: true).printAll;


    l = #[10,-10,10,-10];
	l.split(#[3,15,3], isCyclic: true, overhang: true).printAll;


    l = #[10,-10,10,-10];
	l.split(#[3,15,3], isCyclic: false, overhang: true).printAll;

	l = #[10,10,10, 10].collect { |each| FoscDuration(each) };
	m = l.split(#[3,15,3], isCyclic: false, overhang: true);
	m.do { |each| each.collect { |elem| elem.str }.postln };
	-------------------------------------------------------------------------------------------------------- */
	split { |sums, isCyclic=false, overhang=false|
		var result, currentIndex, currentPiece, currentPieceSum, overage;
		var currentLastElement, needed, lastPiece;
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
    • extendToAbsSum

    [1, 2, 3].extendToAbsSum(10);
    [1, 2, -3].extendToAbsSum(10);
    [1, 2, 3, 99].extendToAbsSum(10);   // truncate if needed
    -------------------------------------------------------------------------------------------------------- */
	extendToAbsSum { |sum|
        var result, difference;
        if (sum <= 0) { throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
        result = this.copy;
        if (result.abs.sum == sum) { ^this };
        difference = sum - (result.abs.sum);
        result = result.add(difference);
        if (result.abs.sum > sum) { result = result.truncateToAbsSum(sum) };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLeaves

    -- mirror selection and iteration methods in FoscObject

    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection
    -------------------------------------------------------------------------------------------------------- */
    doLeaves { |function, pitched, prototype, exclude, doNotIterateGraceContainers=false,
        graceNotes=false, reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).leaves(
            prototype: prototype,
            exclude: exclude,
            doNotIterateGraceContainers: doNotIterateGraceContainers,
            graceNotes: graceNotes,
            pitched: pitched,
            reverse: reverse
        );
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectComponents
   
     -- mirror selection and iteration methods in FoscObject
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection
    -------------------------------------------------------------------------------------------------------- */
    selectComponents { |prototype, exclude, graceNotes=false, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);  
        ^FoscSelection(this).components(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLeaves

    -- mirror selection and iteration methods in FoscObject
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection
    -------------------------------------------------------------------------------------------------------- */
    selectLeaves { |prototype, exclude, graceNotes=false, pitched, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).leaves(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            pitched: pitched,
            reverse: reverse
        );
    
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLogicalTies

     -- mirror selection and iteration methods in FoscObject
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection
    -------------------------------------------------------------------------------------------------------- */
    selectLogicalTies { |exclude, graceNotes=false, nontrivial, pitched, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);   
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

    -- mirror selection and iteration methods in FoscObject
    !!!TODO: make a mixin interface for use by FoscComponent, FoscSelection and SequenceableCollection
    -------------------------------------------------------------------------------------------------------- */
    selectRuns { |exclude|
        FoscObject.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).runs(exclude: exclude);
    }
    /* --------------------------------------------------------------------------------------------------------
	• truncateToAbsSum (abjad: truncate)

	Truncates collection to absolute sum of values.

	Returns new collection.

	l = [-1, 2, -3, 4, -5, 6, -7, 8, -9, 10];
	(1..11).do { |sum| sum.post; " ".post; l.truncateToAbsSum(sum).postln };
	-------------------------------------------------------------------------------------------------------- */
	truncateToAbsSum { |sum|
		var items, total=0, sign, trimmedPart;
		if (sum <= 0) { throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
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
	• truncateToSum (abjad: truncate)

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
		if (sum <= 0) { throw("%:%: sum must be greater than 0.".format(this.species, thisMethod.name)) };
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
    • prIterateBottomUp

    def _iterate_bottom_up(self):
        def recurse(node):
            if isinstance(node, Container):
                for x in node:
                    for y in recurse(x):
                        yield y
            yield node
        return recurse(self)
    -------------------------------------------------------------------------------------------------------- */
    prIterateBottomUp {
        ^this.notYetImplemented(thisMethod);
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
                throw("%:%: index out of range: %.".format(this.species, thisMethod.name, index))
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
            throw("%:%: bad value for 'index': %.".format(this.species, thisMethod.name, index));
        };
    }
}
