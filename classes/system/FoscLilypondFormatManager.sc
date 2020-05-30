/* ------------------------------------------------------------------------------------------------------------
• FoscLilypondFormatManager

Manages Lilypond formatting logic.

n = FoscNote(60, [1, 4]);
//FoscAttach(m = FoscMarkup("groob"), n);
FoscAttach(FoscDynamic('p'), n);
FoscAttach(FoscArticulation('>'), n);
n.prFormatComponent;

b = FoscLilypondFormatManager.bundleFormatContributions(n);
b.before.indicators;
b.opening.indicators;
b.closing.indicators;
b.right.indicators;     // abjad: ('%%% \\p %%%',)
b.right.articulations;

n.wrappers.do { |e| e.prGetFormatPieces.postln };

b.contextSettings;      // not yet implemented
b.grobOverrides;        // not yet implemented
b.grobReverts;          // not yet implemented


e = FoscWrapper(FoscNote(60, 1), FoscArticulation('accent'));
e.indicator;
b = e.prGetFormatPieces; // FoscLilypondFormatBundle
b.right.articulations;
------------------------------------------------------------------------------------------------------------ */
FoscLilypondFormatManager : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	classvar <indent="    ";
	classvar <lilypondColorConstants=#[
		'black', 'blue', 'center', 'cyan', 'darkblue', 'darkcyan', 'darkgreen', 'darkmagenta', 'darkred',
		'darkyellow', 'down', 'green', 'grey', 'left', 'magenta', 'red', 'right', 'up', 'white', 'yellow'
	];
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CLASS METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *bundleFormatContributions

    Gets all format contributions for component.

    Returns Lilypond format bundle.
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *bundleFormatContributions { |component|
		var manager, bundle;
		manager = FoscLilypondFormatManager;
		bundle = FoscLilypondFormatBundle();
		manager.prPopulateIndicatorFormatContributions(component, bundle);
		//manager.prPopulateSpannerFormatContributions(component, bundle);
		manager.prPopulateContextSettingFormatContributions(component, bundle);
        manager.prPopulateGrobOverrideFormatContributions(component, bundle);
        manager.prPopulateGrobRevertFormatContributions(component, bundle);
        bundle.sortOverrides;
		^bundle;
	}
    /* --------------------------------------------------------------------------------------------------------
    • *formatLilypondAttribute

    Formats Lilypond attribute according to Scheme formatting conventions.

    Returns string.


    • Example 1

    Co-ordinate attributes
    
    FoscLilypondFormatManager.formatLilypondAttribute('minimumYExtent');
    FoscLilypondFormatManager.formatLilypondAttribute('xExtent');
    FoscLilypondFormatManager.formatLilypondAttribute('xAlignOnMainNoteheads');
    FoscLilypondFormatManager.formatLilypondAttribute('parentAlignmentY');
    FoscLilypondFormatManager.formatLilypondAttribute('boundDetails_left_stencilAlignDirY');
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *formatLilypondAttribute { |attribute|
        var result, coordinateChars;
        
        if (attribute.isKindOf(FoscLilypondLiteral)) { ^attribute.format };

        result = attribute.asString;
        result = result.replace("__", ".");
        result = result.replace("_", ".");
        result = result.separate { |a, b| b.isUpper };
        coordinateChars = #[$X, $Y, $x, $y]; // capitalize these chars if they specify co-ordinates
        result = result.collect { |each|
            if ((each.size == 1) && { coordinateChars.includes(each[0]) }) { each.toUpper } { each.toLower };
        };
        result = result.join("-");
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *formatLilypondContextSettingInWithBlock

    Formats Lilypond context setting name with value in Lilypond with-block.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    *formatLilypondContextSettingInWithBlock { |name, value|
		var valueParts, result;
		name = name.asString;
		value = FoscLilypondFormatManager.formatLilypondValue(value);
		valueParts = value.split($\n);
		result = "% = %".format(name, valueParts[0]);
        result = [result];
		valueParts[1..].do { |each| result = result.add(FoscLilypondFormatManager.indent ++ each) };
		result = result.join("\n");
		^result;
	}
    /* --------------------------------------------------------------------------------------------------------
    • *formatLilypondContextSettingsInline 

    Formats Lilypond context setting name with value in context.

    Returns string.


    • Example 1

	FoscLilypondFormatManager.formatLilypondContextSettingInline("StaffSymbol.color_", 'red', "Staff");
    -------------------------------------------------------------------------------------------------------- */
    *formatLilypondContextSettingInline { |name, value, context|
		var contextString = "", result;
		name = name.asString;
		value = FoscLilypondFormatManager.formatLilypondValue(value);
		if (context.notNil) { contextString = context.asString ++ "." };
		result = "\\set %% = %".format(contextString, name, value);
		^result;
	}
    /* --------------------------------------------------------------------------------------------------------
    • *formatLilypondValue

    Formats Lilypond argument according to Scheme formatting conventions.

    Returns string.
    

	• Example 1

	FoscLilypondFormatManager.formatLilypondValue(false);
	FoscLilypondFormatManager.formatLilypondValue(\center);
    FoscLilypondFormatManager.formatLilypondValue(FoscSchemeMoment(1, 24));
	FoscLilypondFormatManager.formatLilypondValue(5);
	FoscLilypondFormatManager.formatLilypondValue(\magenta);
	FoscLilypondFormatManager.formatLilypondValue("lilypond::string");
	FoscLilypondFormatManager.formatLilypondValue("lilypondstring");
	FoscLilypondFormatManager.formatLilypondValue("lilypond string");
	FoscLilypondFormatManager.formatLilypondValue([-1, 1]);
	FoscLilypondFormatManager.formatLilypondValue(['font-name', "Times"]);

    FoscLilypondFormatManager.formatLilypondValue(FoscSchemeColor('ForestGreen'));


    FoscMarkup
    -------------------------------------------------------------------------------------------------------- */
	*formatLilypondValue { |expr|
		expr = case
        { expr.respondsTo('prGetLilypondFormat') && { expr.isString.not } } { expr } // pass
		{ expr.isKindOf(Boolean) } { FoscScheme(expr) }
		{ #['up', 'down', 'above', 'below', 'left', 'right', 'center'].includes(expr) } { FoscScheme(expr) }
		{ expr.isKindOf(Number) } { FoscScheme(expr) }
		{ FoscLilypondFormatManager.lilypondColorConstants.includes(expr.asSymbol) } { FoscScheme(expr) }
		{ expr.isString && { expr.contains("::") } } { FoscScheme(expr) }
		{ expr.isString && { expr.contains(" ").not } } { FoscScheme(expr).quoting_("'") }
        { expr.isString && { expr.contains(" ") } } { FoscScheme(expr) }
        { expr.isSequenceableCollection && expr.size == 2 } { FoscSchemePair(*expr) }
		{ FoscScheme(expr).quoting_("'") };
		^expr.format('lilypond');
	}
    /* --------------------------------------------------------------------------------------------------------
    • *makeLilypondOverrideString

	Makes Lilypond override string. Returns string.


    • Example 1

    a = FoscNote(60, 1/4);
    m = override(a);
    m.noteHead.color = 'red';
    m.noteHead.shape = 'square';
    m.prListFormatContributions('override').printAll;
    -------------------------------------------------------------------------------------------------------- */
	// abjad 3.0
    *makeLilypondOverrideString { |grob, attribute, value, context, isOnce=false|
        var result;
        grob = grob.asString.toUpperCamelCase;
        attribute = FoscLilypondFormatManager.formatLilypondAttribute(attribute);
        value = FoscLilypondFormatManager.formatLilypondValue(value);
        context = if (context.notNil) { context.asString.capitalizeFirst ++ "." } { "" };
        isOnce = if (isOnce) { "\\once " } { "" };
        result = "%\\override %%.% = %";
        result = result.format(isOnce, context, grob, attribute, value);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *makeLilypondRevertString

    Makes Lilypond revert string.

    Returns string.

    
    • Example 1

	FoscLilypondFormatManager.makeLilypondRevertString("StaffSymbol", 'color', "Staff");
    -------------------------------------------------------------------------------------------------------- */
	// abjad 3.0
    *makeLilypondRevertString { |grob, attribute, context|
		var dotted, result;
		grob = grob.asString.toUpperCamelCase;
		dotted = FoscLilypondFormatManager.formatLilypondAttribute(attribute);
        if (context.notNil) {
            context = context.asString.toUpperCamelCase ++ ".";
        } {
            context = "";
        };
		result = "\\revert %%.%".format(context, grob, dotted);
		^result;
	}
    /* --------------------------------------------------------------------------------------------------------
    • *makeLilypondTweakString

    Makes Lilypond \tweak string.

    Returns string.

    
    • Example 1
    
    FoscLilypondFormatManager.makeLilypondTweakString('color', 'red', true, 'noteHead');
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *makeLilypondTweakString { |attribute, value, isDirected=true, grob|
        var grobString, result;

        if (grob.notNil) {
            grob = grob.asString.toUpperCamelCase ++ ".";
        } {
            grob = "";
        };

        attribute = FoscLilypondFormatManager.formatLilypondAttribute(attribute);
        value = FoscLilypondFormatManager.formatLilypondValue(value);
        result = "\\tweak %% %".format(grob, attribute, value);
        if (isDirected) { result = "- " ++ result };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *tag

    Tags 'strings' with 'tag'.
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *tag { |strings, tag, deactivate=false|
        var length, localStrings, pad, localTag;

        if (tag.isNil) { ^strings };
        if (strings.isNil) { ^strings };
        assert(deactivate.isKindOf(Boolean));
        length = strings.collect { |string| string.size }.maxItem;
        localStrings = [];
        
        strings.do { |string|
            if (string.contains("%!").not) {
                pad = length - string.size;
            } {
                pad = 0;
            };
            localTag = (" " ! pad) ++ " " ++ "%!" ++ " " ++ (tag.asString);
            string = string ++ localTag;
            localStrings = localStrings.add(string);
        };
        
        if (deactivate) {
            localStrings = localStrings.collect { |string| "%@%" ++ string };
        };
        
        ^localStrings;
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• *prCollectIndicators

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    a.attach(FoscMarkup("foo"));
    a.attach(FoscDynamic('p'));
    c = FoscScore([FoscStaff([b])]);
    c.leafAt(0).attach(FoscTimeSignature([4,4]), context: "Score");
    FoscLilypondFormatManager.prCollectIndicators(a);


    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    a.attach(FoscMarkup("foo"));
    a.attach(FoscDynamic('p'));
    c = FoscScore([FoscStaff([b])]);
    a.attach(FoscTimeSignature([4,4]), context: "Score");
    a.wrappers.do { |each| [each.indicator, each.context].postln };
    c.format;
    -------------------------------------------------------------------------------------------------------- */
	// abjad 3.0
    *prCollectIndicators { |component|
        var wrappers, parentage, result, indicator, indicators;
        var upMarkupWrappers, downMarkupWrappers, neutralMarkupWrappers, contextWrappers, nonContextWrappers;
        wrappers = [];
        component.prGetParentage.do { |parent|
            wrappers = wrappers.addAll(parent.wrappers);
        };
        upMarkupWrappers = [];
        downMarkupWrappers = [];
        neutralMarkupWrappers = [];
        contextWrappers = [];
        nonContextWrappers = [];
        wrappers.do { |wrapper|
            indicator = wrapper.indicator;
            case
            { indicator.respondsTo('prGetLilypondFormat').not
                && { indicator.respondsTo('prGetLilypondFormatBundle').not } } {
                // continue
            }
            { wrapper.annotation.notNil } {
                // continue
            }
            // skip comments and commands unless attached directly to us
            { wrapper.context.isNil
                && { wrapper.indicator.respondsTo('formatLeafChildren') }
                && { wrapper.indicator.formatLeafChildren.not }
                && { wrapper.component != component } } {
                // continue
            }
            // store markup
            { wrapper.indicator.isKindOf(FoscMarkup) } {
                case
                { wrapper.indicator.direction == 'up' } {
                    upMarkupWrappers = upMarkupWrappers.add(wrapper);
                }
                { wrapper.indicator.direction == 'down' } {
                    downMarkupWrappers = downMarkupWrappers.add(wrapper);
                }
                { neutralMarkupWrappers = neutralMarkupWrappers.add(wrapper) }; 
            }
            // store context wrappers
            { wrapper.context.notNil } {
                if (wrapper.annotation.isNil && { wrapper.component === component }) {
                    contextWrappers = contextWrappers.add(wrapper);
                };
            }
            {
                nonContextWrappers = nonContextWrappers.add(wrapper);
            };
        };
        indicators = [
            upMarkupWrappers,
            downMarkupWrappers,
            neutralMarkupWrappers,
            contextWrappers,
            nonContextWrappers
        ];
        ^indicators;
    } 
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateContextSettingFormatContributions


    • Example 1

    a = FoscStaff([FoscNote(60, 1/4)], name: 'foo');
    set(a).instrumentName = FoscMarkup("clar");
    set(a).instrumentName = "clar";
    a.format;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateContextSettingFormatContributions { |component, bundle|
        var result, contextualizer, manager, string;
        result = [];
        manager = FoscLilypondFormatManager;
        if (component.isKindOf(FoscContext)) {
            set(component).vars.keysValuesDo { |name, val|
                string = manager.formatLilypondContextSettingInWithBlock(name, val);
                result = result.add(string);
            } {
                contextualizer = set(component);
                contextualizer.vars.keysValuesDo { |name, val|
                    name = name.asString;
                    if (name[0] == $_) {
                        val.vars.keysValuesDo { |x, y|
                            if (x.asString[0] == $_) {
                                string = manager.formatLilypondContextSettingInWithBlock(x, y, name);
                            };
                            result = result.add(string);
                        };
                    } {
                        string = manager.formatLilypondContextSettingInWithBlock(name, val);
                        result = result.add(string);
                    };
                };
            };
        };
        result = result.sort;
        bundle.contextSettings.addAll(result);
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateGrobOverrideFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateGrobOverrideFormatContributions { |component, bundle|
        var result, isOnce, grob, contributions, pitch, arrow, noteHeadContributions;
        result = [];
        isOnce = component.isKindOf(FoscLeaf);
        grob = override(component);
        contributions = grob.prListFormatContributions('override', isOnce);
        result.do { |each|
            if (each.includes('NoteHead') && { each.includes('pitch') }) { contributions.remove(each) };
        };
        try {
            pitch = component.pitch;
            arrow = pitch.arrow;
        } {
            arrow = nil;
        };
        if (#['up', 'down'].includes(arrow)) {
            noteHeadContributions = pitch.prListFormatContributions;
            contributions = contributions.addAll(noteHeadContributions);
        };
        bundle.grobOverrides.addAll(contributions);
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateGrobRevertFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateGrobRevertFormatContributions { |component, bundle|
        var manager, contributions;
        if (component.isKindOf(FoscLeaf).not) {
            manager = override(component);
            contributions = manager.prListFormatContributions('revert');
            bundle.grobReverts.addAll(contributions);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateIndicatorFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateIndicatorFormatContributions { |component, bundle|
		var manager, upMarkup, downMarkup, neutralMarkup, scopedExpressions, nonScopedExpressions;
		manager = FoscLilypondFormatManager;
        # upMarkup, downMarkup, neutralMarkup, scopedExpressions, nonScopedExpressions = 
            FoscLilypondFormatManager.prCollectIndicators(component);
        manager.prPopulateMarkupFormatContributions(component, bundle, upMarkup, downMarkup, neutralMarkup);
        manager.prPopulateScopedExpressionFormatContributions(component, bundle, scopedExpressions);
        manager.prPopulateNonscopedExpressionFormatContributions(component, bundle, nonScopedExpressions);  
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateMarkupFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prPopulateMarkupFormatContributions { |component, bundle, upMarkupWrappers, downMarkupWrappers,
        neutralMarkupWrappers|
        var direction, markup, formatPieces;
        [upMarkupWrappers, downMarkupWrappers, neutralMarkupWrappers].do { |wrappers|
            wrappers.do { |wrapper|
                if (wrapper.indicator.direction.isNil) {
                    markup = FoscMarkup(wrapper.indicator, direction: '-')
                } {
                    markup = wrapper.indicator;
                };
                formatPieces = markup.prGetFormatPieces;
                formatPieces = FoscLilypondFormatManager.tag(
                    formatPieces,
                    //!!!TODO: wrapper.tag,
                    deactivate: wrapper.deactivate
                );
                bundle.after.markup.addAll(formatPieces);
            };
        };
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateNonscopedExpressionFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateNonscopedExpressionFormatContributions { |component, bundle, nonscopedExpressions|
        var indicator, indicatorBundle;
        nonscopedExpressions.do { |nonscopedExpression|
            indicator = nonscopedExpression.indicator;
            if (indicator.respondsTo('prGetLilypondFormatBundle')) {
                indicatorBundle = indicator.prGetLilypondFormatBundle(component);
                if (indicatorBundle.notNil) { bundle.update(indicatorBundle) };
            };
        };
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateScopedExpressionFormatContributions
    -------------------------------------------------------------------------------------------------------- */
    *prPopulateScopedExpressionFormatContributions { |component, bundle, scopedExpressions|
        var formatPieces, formatSlot;
        scopedExpressions.do { |scopedExpression|
            formatPieces = scopedExpression.prGetFormatPieces;
            if (formatPieces.isKindOf(bundle.species)) {
                bundle.update(formatPieces);
            } {
                formatSlot = scopedExpression.indicator.formatSlot;
                bundle.get(formatSlot).indicators.addAll(formatPieces);
            }; 
        };
	}
    /* --------------------------------------------------------------------------------------------------------
    • *prPopulateSpannerFormatContributions
    -------------------------------------------------------------------------------------------------------- */
 //    *prPopulateSpannerFormatContributions { |component, bundle|
 //        var pairs, spannerBundle, pair, spanner;
 //        pairs = [];
 //        component.prGetParentage.prSpanners.do { |spanner|
 //            spannerBundle = spanner.prGetLilypondFormatBundle(component);
 //            pair = [spanner, spannerBundle];
 //            pairs = pairs.addAll(pair);
 //        };
 //        pairs.pairsDo { |spanner, spannerBundle| bundle.update(spannerBundle) };
	// }
}
