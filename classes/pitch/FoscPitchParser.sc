/* ------------------------------------------------------------------------------------------------------------
• FoscPitchParser

• Example 1

n = [60, 62, 64];
FoscPitchParser(n).do { |each| each.cs.postln };


• Example 2

-  can contain nil (for use in FoscMaker classes)

n = [60, 62, nil, 64];
FoscPitchParser(n).do { |each| each.cs.postln };


• Example 3

n = "Bb4 F#5 C4 <Cb4 E+4 G4> D+5 <C4 E4 G4>";
FoscPitchParser(n).do { |each| each.cs.postln };


• Example 4

n = [60, 62, 64, 'F#5', 'G#5', 'A#5'];
FoscPitchParser(n).do { |each| each.cs.postln };


• Example 5

n = [60, 62, 64, ['F#5', 'G#5', 'A#5']];
FoscPitchParser(n).do { |each| each.cs.postln };


• Example 6: !!!TODO: this should raise an exception

FoscPitchParser(['foo']);
------------------------------------------------------------------------------------------------------------ */
FoscPitchParser : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <pitches; 
    *new { |items|
        var pitches;
        pitches = [];
        case
        { items.isString || items.isKindOf(Symbol) } {
            pitches = FoscPitchStringParser(items);
        }
        {
            items.do { |each|
                case
                { each.isKindOf(FoscPitchSegment) } {
                    pitches = pitches.add(each);  
                }
                { each.isKindOf(FoscPitch) } {
                    pitches = pitches.add(each);  
                }
                { each.isKindOf(FoscNote) } {
                    pitches = pitches.add(each.pitch);  
                }
                { each.isKindOf(FoscChord) } {
                    pitches = pitches.add(each.pitches); 
                }
                { each.isNumber } {
                    pitches = pitches.add(FoscPitch(each)); 
                }
                { each.isString || each.isKindOf(Symbol) } {
                    pitches = pitches.add(FoscPitchStringParser(each)[0]);
                }
                { each.isSequenceableCollection } {
                    pitches = pitches.add(FoscPitchSegment(each));  
                }
                { each.isNil } {
                    pitches = pitches.add(nil);  
                }
                {
                    throw("%:new: can't instantiate with: %.".format(this.species, each));
                };
            };
        };
        ^pitches;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscPitchStringParser

x = "Bb4 F#5 C4 <Cb4 E+4 G4> D+5 <C4 E4 G4>";
FoscPitchStringParser(x).do { |each| each.cs.postln };
------------------------------------------------------------------------------------------------------------ */
FoscPitchStringParser {
    var string, matchingItems, matchedIndices;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |string|
        string = string.asString;
        ^super.new.init(string);
    }
    init { |argString|
        var pitches, result;
        string = argString;
        matchingItems = [];
        matchedIndices = [];
        this.prMatchPitchSegments;
        this.prMatchPitches;
        matchingItems = matchingItems.sort { |a, b| a[0] < b[0] };
        matchingItems = matchingItems.flop[1];
        matchingItems.do { |each|
            case
            { each.isString } {
                result = result.add(FoscPitch(each));
            }
            { each.isArray } {
                pitches = each.collect { |item| FoscPitch(item) };
                result = result.add(FoscPitchSegment(pitches));
            };
        };
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • pitches

    Returns parser result as an array of pitches and/or pitch segments.

    x = "Bb4 A#2 C3 <Cb4 E+4 G4> D+-1 <C4 Eb4 G4>";
    y = FoscPitchStringParser(x);
    y.pitches.printAll;

    x = "Bb4 A#2 C3 C4 Eb4 Gb~4 D~5 C#+4 Eb4 G4";
    y = FoscPitchStringParser(x);
    y.matchingItems.printAll;
    -------------------------------------------------------------------------------------------------------- */
    pitches {
        var pitches, result;
        result = [];
        matchingItems.do { |each|
            case
            { each.isString } {
                result = result.add(FoscPitch(each));
            }
            { each.isArray } {
                pitches = each.collect { |item| FoscPitch(item) };
                result = result.add(FoscPitchSegment(pitches));
            };
        };
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prMatchPitchSegments
    -------------------------------------------------------------------------------------------------------- */
    prMatchPitchSegments {
        var regexBody, match, result, index, str;
        regexBody = "<.*?>";
        match = string.findRegexp(regexBody);
        result = [];
        match.do { |each|
            # index, str = each;
            //!!! use String::pitchNameRegexBody
            regexBody = "[A-G]{1}(?:\\+|#\\+|~|b~|[#]{1,2}|[b]{1,2})?-?[0-9]{1,2}";
            result = result.add([index, str.findRegexp(regexBody).flop[1]]);
            matchedIndices = matchedIndices.addAll((index..(index + str.size - 1)));
        };
        result.do { |each| matchingItems = matchingItems.add(each) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMatchPitches
    -------------------------------------------------------------------------------------------------------- */
    prMatchPitches {
        var regexBody, match;
        //!!! use String::pitchNameRegexBody
        regexBody = "[A-G]{1}(?:\\+|#\\+|~|b~|[#]{1,2}|[b]{1,2})?-?[0-9]{1,2}";
        match = string.findRegexp(regexBody);
        match.do { |each|
            if (matchedIndices.includes(each[0]).not) { matchingItems = matchingItems.add(each) };
        };
    }
}
