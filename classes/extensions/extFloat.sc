/* ------------------------------------------------------------------------------------------------------------
• Float
------------------------------------------------------------------------------------------------------------ */
+ Float {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • partitionByRatio

    Partitions receiver into parts by ratio.

    Returns array of floats.


    • Example 1

    1.5.partitionByRatio(#[1, 1, 1]);


    • Example 2

    -1.5.partitionByRatio(#[1, 1, 1]);


    • Example 3

    1.5.partitionByRatio(#[1, -1, 1]);


    • Example 4

    Support nested rhythm tree syntax.

    1.0.partitionByRatio(#[1, 2, [2, [-2, 2, 1]]]);
    -------------------------------------------------------------------------------------------------------- */
    partitionByRatio { |ratio, precision=1e-12|
        var result, signs, recurse, durs, multiplier, lastIndex, n;

        result = [];
        signs = [];
        
        recurse = { |val, ratio|
            durs = ratio.slice(nil, 0);
            if (durs.isSequenceableCollection.not) { durs = [durs] };
            multiplier = val / durs.abs.sum;
            
            ratio.do { |elem|
                if (elem.isSequenceableCollection) {
                    n = elem[0] * multiplier;
                    recurse.(n, elem[1]);
                } {
                    signs = signs.add(elem.sign);
                    n = (elem.abs * multiplier).round(precision);
                    result = result.add(n);
                };
            };
        };

        recurse.(this, ratio);

        if (this != result.sum) {
            lastIndex = result.lastIndex;
            result[lastIndex] = result[lastIndex] + (this - result.sum);
        };

        result = result.collect { |each, i| each * signs[i] };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionByRatio

    DEPRECATED: version without nested rhythm tree support.
    -------------------------------------------------------------------------------------------------------- */
    // partitionByRatio { |ratio, precision=1e-12|
    //     var result, lastIndex;
    //     result = ratio.abs * (this / ratio.abs.sum);
    //     result = result.round(precision);
    //     if (this != result.sum) {
    //         lastIndex = result.lastIndex;
    //         result[lastIndex] = result[lastIndex] + (this - result.sum);
    //     };
    //     result = result.collect { |each, i| each * ratio[i].sign };
    //     ^result;
    // }
}
