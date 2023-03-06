/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondGrobNameManager (abjad 3.0)

LilyPond grob name manager.

LilyPondGrobNameManager instances are created by the override factory function.


• Example 1

a = FoscStaff();
a.add(FoscLeafMaker().(#[60,62,64,65], [1/4]));
m = override(a);
m.staffSymbol.color = 'red';
a.format;
a.show;


• Example 2

a = FoscNote(60, [1, 4]);
m = override(a);
m.noteHead.color = 'red';
m.noteHead.fontSize = 4;
a.format;
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondGrobNameManager : FoscLilyPondNameManager {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getattr__)

    !!!TODO: deprecate ??
    -------------------------------------------------------------------------------------------------------- */
    // at { |name|
    //     ^vars[name];
    // }
    /* --------------------------------------------------------------------------------------------------------
    • put (abjad: __setattr__)

    !!!TODO: deprecate ??
    -------------------------------------------------------------------------------------------------------- */
    // put { |attribute, value|
    //     attribute = (attribute.asString ++ "_").asSymbol;
    //     this.perform(attribute, value);
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttributeTuples
    
    a = FoscNote(60, 1/4);
    m = override(a);
    m.noteHead.color = 'red';
    m.noteHead.size = 20;
    m.prAttributeTuples;
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prAttributeTuples {
        var result, grob, grobProxy, pairs, attribute, value, triple;
        var context, contextProxy, quadruple;
        result = [];
        vars.keysValuesDo { |name, val|
            if (val.isMemberOf(FoscLilyPondNameManager)) {
                # grob, grobProxy = [name, val];
                pairs = grobProxy.prGetAttributePairs;
                pairs.do { |pair|
                    # attribute, value = pair;
                    triple = [grob, attribute, value];
                    result = result.add(triple);
                };
            } {
                context = name.asString.strip($_).asSymbol;
                contextProxy = value;
                pairs = contextProxy.prGetAttributePairs;
                pairs.do { |pair|
                    # attribute, value = pair;
                    quadruple = [context, grob, attribute, value];
                    result = result.add(quadruple);
                };
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prListFormatContributions
    

    • Example 1

    a = FoscNote(60, [1, 4]);
    m = override(a);
    m.noteHead.color = 'red';
    m.noteHead.shape = 'square';
    m.prListFormatContributions('override');
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prListFormatContributions { |contributionType, isOnce=false|
        var manager, result, context, grob, attribute, value;
        var overrideString, revertString;
        if (['override', 'revert'].includes(contributionType).not) {
            ^throw("%:% % is not a valid contributionType."
                .format(this.species, thisMethod.name, contributionType))
        };
        manager = FoscLilyPondFormatManager;
        result = [];
        this.prAttributeTuples.do { |attributeTuple|
            case
            { attributeTuple.size == 3 } {
                context = nil;
                # grob, attribute, value = attributeTuple;
            }
            { attributeTuple.size == 4 } {
                # context, grob, attribute, value = attributeTuple;
            }
            { 
                ^throw("%:% invalid attribute tuple: %."
                    .format(this.species, thisMethod.name, attributeTuple));
            };
            if (contributionType == 'override') {
                overrideString = manager.makeLilypondOverrideString(grob, attribute, value, context, isOnce);
                result = result.add(overrideString);
            } {
                revertString = manager.makeLilypondRevertString(grob, attribute, context);
                result = result.add(revertString);
            };  
        };
        result = result.sort;
        ^result;
    }
}
