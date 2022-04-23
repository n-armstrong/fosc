/* ------------------------------------------------------------------------------------------------------------
• Integer
------------------------------------------------------------------------------------------------------------ */
+ Integer {
    /* --------------------------------------------------------------------------------------------------------
    • partitionByRatio

    Partitions receiver into nearest integer parts by ratio.

    Returns array of integers.
    

    • Example 1

    10.partitionByRatio(#[1, 2]);


    • Example 2

    -10.partitionByRatio(#[1, 2]);


    • Example 3

    10.partitionByRatio(#[1]);


    • Example 4

    10.partitionByRatio(#[1, -1, -1]);


    • Example 5

    10.partitionByRatio(#[0.3, 0.1]);


    • Example 6

    10.partitionByRatio(1 ! 20);
    -------------------------------------------------------------------------------------------------------- */
    partitionByRatio { |ratio|
        var result, divisions, cumulativeDivisions, roundedDivision, ratioSigns;
        
        result = [0];
        divisions = ratio.collect { |part| (this.abs.asFloat * abs(part)) / ratio.abs.sum };
        cumulativeDivisions = divisions.offsets[1..];
        
        cumulativeDivisions.do { |division|
            roundedDivision = division.round.asInteger - (result.sum);
            if ((division - division.round) == 0.5) { roundedDivision = roundedDivision + 1 };
            result = result.add(roundedDivision);
        };
        
        result = result[1..];
        if (this.sign == -1) { result = result.collect { |each| each.neg } };
        ratioSigns = ratio.collect { |each| each.sign };
        result = result.collect { |each, i| (each * ratioSigns[i]).asInteger };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionIntoCanonicParts

    Partitions integer into canonic parts.

    Returns array of one or more integers.


    • Example 1

    (1..16).do { |n| n.post; Post.tab; n.partitionIntoCanonicParts.postln };
    -------------------------------------------------------------------------------------------------------- */
    partitionIntoCanonicParts { |decreaseMonotonic=true|
        var result, previousEmpty, binaryN, binaryLength, placeValue;
        
        if (this == 0) { ^0 };
        result = [];
        previousEmpty = true;
        binaryN = this.abs.asBinaryString;
        binaryN = binaryN[binaryN.indexOf($1)..];
        binaryLength = binaryN.size;
        
        binaryN.do { |x, i|
            if (x == $1) {
                placeValue = 2 ** (binaryLength - i - 1);
                if (previousEmpty) {
                    result = result.add(placeValue);
                } {
                    result[result.lastIndex] = result[result.lastIndex] + placeValue;
                };
                previousEmpty = false;
            } {
                previousEmpty = true;
            };
        };
        
        result = result.collect { |each| (each * this.sign).asInteger };
        
        if (decreaseMonotonic) {
            ^result;
        } {
            ^result.reverse;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionIntoMaximallyEvenParts

    Partitions integer into m maximally even parts using the Björklund algorithm.

    Returns array of one or more integers.
    

    • Example 1

    16.partitionIntoMaximallyEvenParts(7);
    -------------------------------------------------------------------------------------------------------- */
    partitionIntoMaximallyEvenParts { |m|
        var pattern, counts, remainders, divisor, level, recurse, index, result;

        assert(
            m.isInteger && { m > 0 } && { m <= this },
            "%:%: 'm' must be a positive integer less than or equal to receiver: %."
                .format(this.species, thisMethod.name, m)
        );

        recurse = { |level|
            var i = 0;
            switch(level,  
                -1, { pattern = pattern.add(0) },
                -2, { pattern = pattern.add(1) },
                {
                    while { i < counts[level] } {
                        recurse.(level - 1);
                        i = i + 1;
                    };
                    if (remainders[level] != 0) { recurse.(level - 2) };
                }
            );
        };

        pattern = [];  
        counts = [];
        remainders = [m];
        divisor = this - m;
        level = 0;

        while { remainders[level] > 1 } {
            counts = counts.add((divisor / remainders[level]).floor.asInteger);
            remainders = remainders.add(divisor % remainders[level]);
            divisor = remainders[level];
            level = level + 1;
        };

        counts = counts.add(divisor);
        recurse.(level);
        
        index = pattern.indexOf(1);
        pattern = pattern[index..] ++ pattern[..(index - 1)];
        result = pattern.separate { |a, b| b == 1 };
        result = result.collect { |each| each.size };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionIntoPartsLessThanDouble

    Partitions integer into parts less than double integer m.

    Returns array of one or more integers.

    (1..24).do { |n| n.post; Post.tab; n.partitionIntoPartsLessThanDouble(4).postln };
    -------------------------------------------------------------------------------------------------------- */
    partitionIntoPartsLessThanDouble { |m|
        var result, currentValue, doubleM;
        
        m = m.asInteger;
        result = [];
        currentValue = this;
        doubleM = 2 * m;
        
        while { doubleM <= currentValue } {
            result = result.add(m);
            currentValue = currentValue - m;
        };
        
        result = result.add(currentValue);
        
        ^result;
    }
}
