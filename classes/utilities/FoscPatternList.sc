/* ------------------------------------------------------------------------------------------------------------
• FoscPatternList

a = FoscPattern(#[0,1,7], period: 16);
b = FoscPattern(#[2,4,5], period: 15);
p = FoscPatternList([a, b]);
p.items;
------------------------------------------------------------------------------------------------------------ */
FoscPatternList : FoscTypedList {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |items|
        ^super.new(items, FoscPattern);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • getMatchingPattern

    Gets pattern matching 'index'.


    • Example 1

    Get patterns that match the first ten indices.

    p = FoscPatternList([
        FoscPattern(#[1], 2),
        FoscPattern(#[-3,-2,-1]),
    ]);
    
    10.do { |i| Post << i << Char.space; p.getMatchingPattern(i, 10).cs.postln };


    Get patterns that match the next ten indices. Note that the last three indices no longer match the second pattern.

    p = FoscPatternList([
        FoscPattern(#[1], 2),
        FoscPattern(#[-3,-2,-1]),
    ]);

    (10..19).do { |i| Post << i << Char.space; p.getMatchingPattern(i, 10).cs.postln };


    Get patterns that match the first ten indices with 'rotation' set to 1.

    p = FoscPatternList([
        FoscPattern(#[1], 2),
        FoscPattern(#[-3,-2,-1]),
    ]);

    10.do { |i| Post << i << Char.space; p.getMatchingPattern(i, 10, rotation: 1).cs.postln };


    p = FoscPatternList([
        FoscSilenceMask(FoscPattern(#[0,4,5])),
        FoscSustainMask(FoscPattern(#[1,11,13]))
    ]);
    
    20.do { |i| p.getMatchingPattern(i, 20).postln; };
    -------------------------------------------------------------------------------------------------------- */
    getMatchingPattern { |index, totalLength, rotation|
        this.items.reverseDo { |pattern|
            case 
            { pattern.respondsTo('pattern') && { pattern.pattern.notNil } } {
                if (pattern.pattern.matchesIndex(index, totalLength, rotation)) {
                    ^pattern;
                };
            }
            { pattern.matchesIndex(index, totalLength, rotation) } {
                ^pattern;
            };
        };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • getMatchingPayload

    Gets payload attached to pattern matching 'index'.


    • Example 1

    Get patterns that match the first ten indices.

    p = FoscPatternList([
        FoscPattern(#[0], period: 2, payload: 'staccato'),
        FoscPattern(#[-3,-2,-1], payload: 'tenuto'),
    ]);

    10.do { |i| Post << i << Char.space; p.getMatchingPayload(i, 10).postln };


    p = FoscPatternList([
        FoscSilenceMask(FoscPattern(#[0,4,5])),
        FoscSustainMask(FoscPattern(#[1,11,13]))
    ]);
    
    20.do { |i| p.getMatchingPayload(i, 20).postln; };
    -------------------------------------------------------------------------------------------------------- */
    getMatchingPayload { |index, totalLength, rotation|
        var pattern, payload;
        pattern = this.getMatchingPattern(index, totalLength, rotation);
        if (pattern.notNil) { payload = pattern.payload };
        ^payload;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prItemCoercer
    -------------------------------------------------------------------------------------------------------- */
    prItemCoercer { |object|
        case 
        {
            object.isKindOf(Pattern).not
            && { object.respondsTo('pattern') }
            && { object.pattern.notNil }
        } {
            // pass
        }
        { object.isKindOf(FoscPattern).not } {
            object = FoscPattern(object);
        };
        ^object;
    }
}
