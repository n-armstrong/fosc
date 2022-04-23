/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondGrobOverride (abjad 3.0)

LilyPond grob override.
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondGrobOverride : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <lilypondType, <grobName, <isOnce, <isRevert, <propertyPath, <value;
    var <formatLeafChildren=false;
    *new { |lilypondType, grobName='NoteHead', isOnce=false, isRevert=false, propertyPath='color', value='red'|
        if (lilypondType.notNil) { lilypondType = lilypondType.asSymbol };
        assert(grobName.notNil, receiver: this, method: thisMethod);
        grobName = grobName.asSymbol;
        if ([Symbol, String].any { |type| propertyPath.isKindOf(type) }) { propertyPath = [propertyPath] };
        ^super.new.init(lilypondType, grobName, isOnce, isRevert, propertyPath, value);
    } 
    init { |argLilypondType, argGrobName, argIsOnce, argIsRevert, argPropertyPath, argValue|
        lilypondType = argLilypondType;
        grobName = argGrobName;
        isOnce = argIsOnce;
        isRevert = argIsRevert;
        propertyPath = argPropertyPath;
        value = argValue;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • grobName

    LilyPond grob override grob name.
    
    Returns Symbol.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(grobName: 'Glissando');
    a.grobName;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isOnce

    Is true when grob override is to be applied only once.

    Returns true or false.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride("Staff", "NoteHead", isOnce: true);
    a.isOnce;


    • Example 2

    a = FoscLilyPondGrobOverride(grobName: 'Glissando', isOnce: false);
    a.isOnce;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isRevert

    Is true when grob override is a grob revert.

    Returns true or false.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride("Staff", "NoteHead", isRevert: true);
    a.isRevert;


    • Example 2

    a = FoscLilyPondGrobOverride(grobName: 'Glissando', isRevert: false);
    a.isRevert;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lilypondType

    Optional Lilypond grob override context name.
    
    Returns Symbol or nil.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride("Staff", "NoteHead", true, false, 'color', 'red');
    a.lilypondType;


    • Example 2

    a = FoscLilyPondGrobOverride(grobName: 'Glissando');
    a.lilypondType == nil;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • overrideFormatPieces

    Gets LilyPond grob override \override format pieces.
    
    Returns array of strings.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        lilypondType: "Staff",
        grobName: "TextSpanner",
        isOnce: true,
        propertyPath: #['bound-details', 'left', 'text'],
        value: FoscMarkup("\\bold { over pressure }")
    );
    a.overrideFormatPieces.printAll;
    -------------------------------------------------------------------------------------------------------- */
    overrideFormatPieces {
        var result, valuePieces;
        result = [];
        if (isOnce) { result = result.add("\\once") };
        result = result.add("\\override");
        result = result.add(this.prOverridePropertyPathString);
        result = result.add("=");
        valuePieces = FoscScheme.formatEmbeddedSchemeValue(value);
        valuePieces = valuePieces.split("\n");
        result = result.add(valuePieces[0]);
        result = [result.join(" ")];
        result = result.addAll(valuePieces[1..]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • overrideString
    
    Gets LilyPond grob override \override string.
            
    Returns string.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        grobName: "Glissando",
        propertyPath: 'style',
        value: FoscSchemeSymbol('zigzag')
    );
    a.overrideString;
    -------------------------------------------------------------------------------------------------------- */
    overrideString {
        ^this.overrideFormatPieces.join("\n");
    }
    /* --------------------------------------------------------------------------------------------------------
    • propertyPath

    LilyPond grob override property path.
    
    Returns array of symbols.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        lilypondType: "Staff",
        grobName: "TextSpanner",
        isOnce: true,
        propertyPath: #['bound-details', 'left', 'text'],
        value: FoscMarkup("\\bold { over pressure }")
    );
    a.propertyPath[0];
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • revertFormatPieces

    Gets LilyPond grob override \revert format pieces.
    
    Returns array of strings.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        grobName: "Glissando",
        propertyPath: 'style',
        value: FoscSchemeSymbol('zigzag')
    );
    a.revertFormatPieces;
    -------------------------------------------------------------------------------------------------------- */
    revertFormatPieces {
        var result;
        result = "\\revert %".format(this.prRevertPropertyPathString);
        ^[result];
    }
    /* --------------------------------------------------------------------------------------------------------
    • revertString

    Gets LilyPond grob override \revert string.
            
    Returns string.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        grobName: "Glissando",
        propertyPath: 'style',
        value: FoscSchemeSymbol('zigzag')
    );
    a.revertString;
    -------------------------------------------------------------------------------------------------------- */
    revertString {
        ^this.revertFormatPieces.join("\n");
    }
    /* --------------------------------------------------------------------------------------------------------
    • value

    Value of LilyPond grob override.

    Returns arbitrary object.
    
    
    • Example 1

    a = FoscLilyPondGrobOverride(
        lilypondType: "Staff",
        grobName: "TextSpanner",
        isOnce: true,
        propertyPath: #['bound-details', 'left', 'text'],
        value: FoscMarkup("\\bold { over pressure }")
    );
    a.value;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • tweakString


    • Example 1

    a = FoscLilyPondGrobOverride(
        grobName: "Glissando",
        propertyPath: 'style',
        value: FoscSchemeSymbol('zigzag')
    );
    a.tweakString;


    • Example 2

    a = FoscLilyPondGrobOverride(
        grobName: "RehearsalMark",
        propertyPath: 'color',
        value: 'red'
    );
    a.tweakString;


    • Example 3

    Lilypond literals are allowed.

    a = FoscLilyPondGrobOverride(
        grobName: "TextSpan",
        propertyPath: #['bound-details', 'left-broken', 'text'],
        value: FoscLilyPondLiteral("\\markup \\upright pont.")
    );
    a.tweakString;
    -------------------------------------------------------------------------------------------------------- */
    tweakString { |isDirected=true, grob=false|
        var result, propertyPath, string;

        result =  if (isDirected) { ["- \\tweak"] } { ["\\tweak"] };
        
        if (grob) {
            propertyPath = [grobName] ++ this.propertyPath;
        } {
            propertyPath = this.propertyPath;
        };
        
        string = propertyPath.join(".");
        result = result.add(string);
        
        if (value.isKindOf(FoscLilyPondLiteral)) {
            value.name.class.postln;
            assert(
                [String, Symbol].any { |type| value.name.isKindOf(type) },
                receiver: this,
                method: thisMethod
            );
            string = value.name;
        } {
            string = FoscScheme.formatEmbeddedSchemeValue(value);
        };

        result = result.add(string);
        result = result.join(" ");
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when 'object' is a FoscLilyPondGrobOverride with equivalent instance variable values.
    

    • Example 1

    a = FoscLilyPondGrobOverride("Staff", "NoteHead", true, false, 'color', 'red');
    b = FoscLilyPondGrobOverride("Staff", "NoteHead", true, false, 'color', 'red');
    a == b;


    • Example 2

    a = FoscLilyPondGrobOverride("Staff", "NoteHead", true, false, 'color', 'red');
    b = FoscLilyPondGrobOverride("Voice", "NoteHead", true, false, 'color', 'red');
    a == b;
    -------------------------------------------------------------------------------------------------------- */
    == { |object|
        if (object.isMemberOf(this.species).not) { ^false };
        if (this.instVarDict != object.instVarDict) { ^false };
        ^true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, revertFormat, overrideFormat;
        bundle = FoscLilyPondFormatBundle();
        if (isOnce.not) {
            revertFormat = this.revertFormatPieces.join("\n");
            bundle.grobReverts.add(revertFormat);
        };
        if (isRevert.not) {
            overrideFormat = this.overrideFormatPieces.join("\n");
            bundle.grobOverrides.add(overrideFormat);
        };
        ^bundle;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prOverridePropertyPathString
    -------------------------------------------------------------------------------------------------------- */
    prOverridePropertyPathString {
        var parts, path;
        parts = [];
        if (lilypondType.notNil) { parts = parts.add(lilypondType) };
        parts = parts.add(grobName);
        parts = parts.addAll(propertyPath);
        path = parts.join(".");
        ^path;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRevertPropertyPathString
    -------------------------------------------------------------------------------------------------------- */
    prRevertPropertyPathString {
        var parts, path;
        parts = [];
        if (lilypondType.notNil) { parts = parts.add(lilypondType) };
        parts = parts.add(grobName);
        parts = parts.add(propertyPath[0]);
        path = parts.join(".");
        ^path;
    }
}
