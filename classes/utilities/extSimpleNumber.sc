/* ------------------------------------------------------------------------------------------------------------
• SimpleNumber

SimpleNumber.dumpInterface

12.format
------------------------------------------------------------------------------------------------------------ */
+ SimpleNumber {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    x = Array.geom(20, 1000, 7/10);
    x.do { |n| n.format(precision: 3, width: 8).postln };

    Returns a string.

    precision: number of decimal places
    width: column width
    -------------------------------------------------------------------------------------------------------- */
    format { |precision=2, width=6|
        var characteristic, mantissa, result;
        characteristic = this.asInteger.asString;
        mantissa = this.frac.round(10 ** precision.neg).asString.drop(2).padRight(precision, "0");
        if (precision >= 1) {
            result = "%.%".format(characteristic, mantissa).padLeft(width, " ");
        } {
            result = "%".format(characteristic).padLeft(width, " ");   
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • harmonics

    36.midicps.harmonics.cpsmidi.round(1/6).round(0.01).printAll;
    36.midicps.harmonics(indices: (1,3..29)).cpsmidi.round(1/6).round(0.01).printAll;
    -------------------------------------------------------------------------------------------------------- */
    harmonics { |n=32|
        ^n.collect { |mul| this * mul };
    }
    /* --------------------------------------------------------------------------------------------------------
    • subharmonics

    84.midicps.subharmonics.cpsmidi.round(1/6).round(0.01).printAll;
    84.midicps.subharmonics(indices: (1,3..29)).cpsmidi.round(1/6).round(0.01).printAll;
    -------------------------------------------------------------------------------------------------------- */
    subharmonics { |indices|
        indices = indices ?? { Array.series(16, 1) };
        ^indices.collect { |weight| this / weight };
    }
}
