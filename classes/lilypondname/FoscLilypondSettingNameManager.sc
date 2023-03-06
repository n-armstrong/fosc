/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondSettingNameManager (abjad 3.0)

LilyPond setting name manager.


• Example 1

a = FoscNote(60, 1/4);
set(a);             // a FoscLilyPondSettingNameManager
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondSettingNameManager : FoscLilyPondNameManager {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getattr__)
    -------------------------------------------------------------------------------------------------------- */
    at { |name|
        var camelName, typeName, context;
        camelName = name.asString.toUpperCamelCase;
        case
        { name[0] == $_ } {
            try {
                ^vars[name.asSymbol];
            } {
                typeName = this.name.asString;
                ^throw("% has not attribute: %".format(typeName, name));
            };
        }
        // !!!TODO: contexts import from ly files not yet implemented
        // { contexts.includes(camelName) } {
        //     try {
        //         ^vars[("_" ++ name).asSymbol];
        //     } {
        //         context = FoscLilyPondManager();
        //         vars[("_" ++ name).asSymbol] = context;
        //         ^context;
        //     };
        // }
        {
            try {
                ^vars[name.asSymbol];
            } {
                typeName = this.name.asString;
                ^throw("% has not attribute: %".format(typeName, name));
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttributeTuples
    -------------------------------------------------------------------------------------------------------- */
    prAttributeTuples {
        var result, prefixedContextName, lilypondType, contextProxy, pairs, triple;
        var attributeName, attributeValue;
        result = [];
        vars.keysValuesDo { |name, val|
            if (val.isMemberOf(FoscLilyPondNameManager)) {
                prefixedContextName = name;
                lilypondType = prefixedContextName.asString.strip("_");
                contextProxy = val;
                pairs = contextProxy.prGetAttributePairs;
                pairs.do { |pair|
                    # attributeName, attributeValue = pair;
                    triple = [lilypondType, attributeName, attributeValue];
                    result = result.add(triple);
                };
            } {
                result = result.add([name, val]);
            };
        };
        ^result;
    }
}
