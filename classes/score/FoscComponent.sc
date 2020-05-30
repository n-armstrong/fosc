/* ------------------------------------------------------------------------------------------------------------
• FoscComponent
------------------------------------------------------------------------------------------------------------ */
FoscComponent : FoscObject {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <indicatorsAreCurrent=false, <isForbiddenToUpdate=false, <overrides, <lilypondSettingNameManager;
    var <measureNumber, <offsetsAreCurrent=false, <offsetsInSecondsAreCurrent=false, <parent, <player; 
    var <startOffset, startOffsetInSeconds, <stopOffset, stopOffsetInSeconds, <tag, <timespan, <wrappers;
	*new { |tag|
        if (tag.notNil) { assert([String, Symbol].any { |type| tag.isKindOf(type) }) };
		^super.new.initFoscComponent(tag);
	}
	initFoscComponent { |argTag|
		tag = argTag;
		timespan = FoscTimespan();
		wrappers = List[];
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • startOffsetInSeconds

    Gets start offset in seconds. If no effective metronome mark is found, tempo is set to 1/4 = 60.
    -------------------------------------------------------------------------------------------------------- */
    startOffsetInSeconds {
        if (startOffsetInSeconds.notNil) {
            ^startOffsetInSeconds;
        } {
            ^FoscMetronomeMark(#[1,4], 60).durationToSeconds(startOffset);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopOffsetInSeconds

    Gets stop offset in seconds. If no effective metronome mark is found, tempo is set to 1/4 = 60.
    -------------------------------------------------------------------------------------------------------- */
    stopOffsetInSeconds {
        if (stopOffsetInSeconds.notNil) {
            ^stopOffsetInSeconds
        } {
            ^FoscMetronomeMark(#[1,4], 60).durationToSeconds(stopOffset);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • tag

    Gets component tag.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation.

    Subclass responsibility.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Shallow copies component.

    Copies indicators.

    Does not copy spanners.

    Does not copy children.

    Returns new component.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.copy;
    b.components;

    • copy with children

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.prCopyWithChildren;
    b.components;
    b.show;

    a.doComponents { |each| each.postln };
    b.doComponents { |each| each.postln }
    -------------------------------------------------------------------------------------------------------- */
    copy {
        var newComponent, manager;
        newComponent = this.species.new(*this.storeArgs);
        if (this.respondsTo('overrides') && { this.overrides.notNil }) {
            manager = override(this).copy;
            newComponent.instVarPut('overrides', manager);
        };
        if (this.respondsTo('lilypondSettingNameManager') && { this.lilypondSettingNameManager.notNil }) {
            manager = set(this).copy;
            newComponent.instVarPut('lilypondSettingNameManager', manager);
        };
        FoscInspection(this).annotationWrappers.do { |wrapper|
            newComponent.attach(wrapper.copy);
        };
        wrappers.do { |wrapper|
            newComponent.attach(wrapper.copy);
        };
        ^newComponent;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    Formats component.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates component.

    Returns LilyPond file.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        var stylesheetPath, includes, lilypondFile;
        stylesheetPath = "%/default.ily".format(FoscConfiguration.foscStylesheetDirectory);
        if (File.exists(stylesheetPath).not) {
            warn("Default stylesheet not found at: %.".format(stylesheetPath));
        } {
            includes = [stylesheetPath];
        };
        lilypondFile = FoscLilypondFile(this, includes: includes);
        ^lilypondFile;
    }
	/* --------------------------------------------------------------------------------------------------------
    • storeArgs (abjad: __getnewargs__)
    
    Gets new arguments.

    Returns array.
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCacheNamedChildren
    -------------------------------------------------------------------------------------------------------- */
    prCacheNamedChildren {
        var nameDictionary;
        nameDictionary = ();
        if (this.respondsTo('namedChildren') && { this.namedChildren.notNil }) {
            this.namedChildren.keysValuesDo { |name, children|
                nameDictionary[name] = children.copy;
            };
        };
        if (this.respondsTo('name') && { this.name.notNil }) {
            if (nameDictionary.keys.includes(this.name).not) { nameDictionary[this.name] = [] };
            nameDictionary[this.name] = nameDictionary[this.name].add(this);
        };
        ^nameDictionary;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCheckForCycles

    a = FoscVoice([FoscNote(60, 1/4)]);
    a.add(FoscNote(67, 3/4));
    a.format

    FoscParentage
    -------------------------------------------------------------------------------------------------------- */
    prCheckForCycles { |components|
        var parentage;
        parentage = this.prGetParentage;
        components.do { |component| if (parentage.includes(component)) { ^true } };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prExtract

    see FoscMutation: extract for examples
    -------------------------------------------------------------------------------------------------------- */
    prExtract { |scaleContents=false|
        var selection, parent, start, stop, components;
        if (scaleContents) { this.prScaleContents(this.multiplier) };
        selection = FoscSelection([this]);
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        if (parent.isNil) {
            throw("%:%: can't extract a component without a parent.".format(this.species, thisMethod.name));
        };
        components = if (this.respondsTo('components')) { this.components } { [] };
        parent.prSetItem((start..(stop + 1)), components);
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAbsoluteAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAbsoluteAfterSlot { |bundle|
        var result;
        result = result.add(['literals', bundle.absoluteAfter.commands]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAbsoluteBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAbsoluteBeforeSlot { |bundle|
        var result;
        result = result.add(['literals', bundle.absoluteBefore.commands]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAfterSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatBeforeSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatCloseBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatCloseBracketsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatClosingSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatClosingSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatComponent

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscNote(60, 1/4)]);
    a.prGetFormatPieces(true);
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatComponent { |pieces=false|
        var result, contributions, bundle, contributor, contribution;
        result = [];
        contributions = [];
        bundle = FoscLilypondFormatManager.bundleFormatContributions(this);
        result = result.addAll(this.prFormatAbsoluteBeforeSlot(bundle));
        result = result.addAll(this.prFormatBeforeSlot(bundle));
        result = result.addAll(this.prFormatOpenBracketsSlot(bundle));
        result = result.addAll(this.prFormatOpeningSlot(bundle));
        result = result.addAll(this.prFormatContentsSlot(bundle));
        result = result.addAll(this.prFormatClosingSlot(bundle));
        result = result.addAll(this.prFormatCloseBracketsSlot(bundle));
        result = result.addAll(this.prFormatAfterSlot(bundle));
        result = result.addAll(this.prFormatAbsoluteAfterSlot(bundle));
        result.do { |each|
            # contributor, contribution = if (each.size == 1) { each[0] } { each };
            contributions = contributions.addAll(contribution) 
        };
        if (pieces) { ^contributions } { ^contributions.join("\n") };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContributionsForSlot
    -------------------------------------------------------------------------------------------------------- */
    // DEPRECATED
    // prFormatContributionsForSlot { |slotIdentifier, bundle|
    //     var result, manager, slotNames, slotIndex, slotName, methodName, method;
    //     result = [];
    //     if (bundle.isNil) {
    //         manager = FoscLilypondFormatManager;
    //         bundle = manager.bundleFormatContributions(this);
    //     };
    //     slotNames = #[
    //         'before',
    //         'openBrackets',
    //         'opening',
    //         'contents',
    //         'closing',
    //         'closeBrackets',
    //         'after'
    //     ];
    //     case
    //     { slotIdentifier.isInteger } {
    //         if (slotIdentifier.inclusivelyBetween(1, 7).not) {
    //             throw("%:%: slotIdentifier must be between 1 and 7.".format(this.species, thisMethod.name));
    //         };
    //         slotIndex = slotIdentifier - 1;
    //         slotName = slotNames[slotIndex];
    //     }
    //     { [String, Symbol].any { |type| slotIdentifier.isKindOf(type) } } {
    //         if (slotNames.includes(slotName).not) {
    //             throw("%:% % is not in slotNames.".format(this.species, thisMethod.name, slotName));
    //         };
    //     };
    //     methodName = "prFormat%Slot".format(slotName.asString.capitalizeFirst);
    //     methodName = methodName.asSymbol;
    //     this.perform(methodName, bundle).do { |each|
    //         result = result ++ each[0];
    //     };
    //     ^result;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContentsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatContentsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpeningSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpeningSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetContents
    -------------------------------------------------------------------------------------------------------- */
    prGetContents {
        var result;
        result = [this];
        if (this.respondsTo('components') && { this.components.notNil }) {
            result = result.addAll(this.components);
        };
        result = FoscSelection(result);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDescendantsStartingWith
    -------------------------------------------------------------------------------------------------------- */
    prGetDescendantsStartingWith {
        var result;
        result = [this];
        if (this.isKindOf(FoscContainer)) {
            if (this.isSimultaneous) {
                this.do { |each| result = result ++ each.prGetDescendantsStartingWith };
            } {
                result = result ++ this[0].prGetDescendantsStartingWith;
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDescendantsStoppingWith
    -------------------------------------------------------------------------------------------------------- */
    prGetDescendantsStoppingWith {
        var result;
        result = [this];
        if (this.isKindOf(FoscContainer)) {
            if (this.isSimultaneous) {
                this.do { |each| result = result ++ each.prGetDescendantsStoppingWith };
            } {
                result = result ++ this[this.size - 1].prGetDescendantsStoppingWith;
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDuration
 
    a = FoscNote(60, 1/4);
    a.prGetDuration.str;
    a.prGetDuration(inSeconds: true).asFloat; // defaults to mm. 1/4 = 60

    a = FoscStaff(FoscLeafMaker().(60 ! 7, [1/4]));
    a.prGetDuration.str;

    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    a[0].prGetDuration.str;
    a[0].prGetDuration(inSeconds: true).asFloat; // defaults to mm. 1/4 = 60
    -------------------------------------------------------------------------------------------------------- */
    prGetDuration { |inSeconds=false|
        case
        { inSeconds } {
            ^this.prGetDurationInSeconds;
        }
        { parent.isNil } {
            ^this.prGetPreprolatedDuration;
        }
        {
            ^(parent.prGetParentage.prolation * this.prGetPreprolatedDuration);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetEffective

    • dynamics persist

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    a[2].attach(FoscDynamic('f'));
    a.doLeaves { |leaf| leaf.prGetEffective(FoscDynamic).cs.postln };

    • metronome marks persist
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = FoscScore([a]);
    b.leafAt(0).attach(FoscMetronomeMark(#[1,4], 60));
    b.leafAt(2).attach(FoscMetronomeMark(#[1,4], 90));
    a.doLeaves { |leaf| leaf.prGetEffective(FoscMetronomeMark).unitsPerMinute.postln };
    -------------------------------------------------------------------------------------------------------- */
    prGetEffective { |prototype, attributes, command, n=0, unwrap=true|
        var candidateWrappers, parentage, enclosingVoiceName, localWrappers, appendWrapper=false, offset;
        var allOffsets, index, wrapper;

        this.prUpdateNow(indicators: true);
        candidateWrappers = ();
        parentage = this.prGetParentage(graceNotes: true);

        parentage.do { |component|
            if (component.isKindOf(FoscVoice)) {
                if (enclosingVoiceName.notNil && { component.name != enclosingVoiceName }) {
                    // continue
                } {
                    enclosingVoiceName = component.name ?? { component.hash };
                };
            };
            localWrappers = [];

            component.wrappers.do { |wrapper|          
                case { wrapper.annotation.notNil } {
                    // continue
                }
                { wrapper.indicator.isKindOf(prototype) } {
                    appendWrapper = true;
                    if (command.notNil && { wrapper.indicator.command != command }) {
                        // contimue
                    } {
                        if (attributes.notNil) {
                            attributes.keysValuesDo { |name, val|
                                if (wrapper.indicator.respondsTo('name') && { wrapper.name != val }) {
                                    appendWrapper = false;
                                };
                            };
                        };
                        if (appendWrapper) { localWrappers = localWrappers.add(wrapper) };
                    };
                };
            };

            if (
                localWrappers.any { |wrapper| wrapper.deactivate }
                && { localWrappers.all { |wrapper| wrapper.deactivate }.not }
            ) {
                localWrappers = localWrappers.select { |each| each.deactivate.not };
            };

            localWrappers.do { |wrapper|
                offset = wrapper.startOffset;
                if (candidateWrappers[offset].isNil) { candidateWrappers[offset] = List[] };
                candidateWrappers[offset].add(wrapper);
            };

            case
            { component.isKindOf(FoscContext).not } {
                // continue
            } {
                component.dependentWrappers.do { |wrapper|
                    case 
                    { wrapper.annotation.notNil } {
                        // continue
                    } {
                        if (wrapper.indicator.isKindOf(prototype)) {
                            appendWrapper = true;
                            case { command.notNil && { wrapper.indicator.command != command } } {
                                // continue
                            } {
                                if (attributes.notNil) {
                                    attributes.keysValuesDo { |name, val|
                                        if (
                                            wrapper.indicator.respondsTo('name')
                                            && { wrapper.name != val })
                                        {
                                            appendWrapper = false;
                                        };
                                    };
                                };
                                if (appendWrapper) {
                                    offset = wrapper.startOffset;
                                    if (candidateWrappers[offset].isNil) { 
                                        candidateWrappers[offset] = List[];
                                    };
                                    candidateWrappers[offset].add(wrapper);
                                };
                            };
                        };
                    };
                };
            };
        };

        if (candidateWrappers.isEmpty) { ^nil };

        allOffsets = candidateWrappers.keys.as(Array).sort;
        startOffset = this.prGetTimespan.startOffset;
        index = allOffsets.bisect(startOffset) - 1 + (n.asInteger);
        if (index < 0) {
            ^nil;
        } {
            if (candidateWrappers.size <= index) { ^nil };
        };
        wrapper = candidateWrappers[allOffsets[index]][0];
        if (unwrap) { ^wrapper.indicator };
        ^wrapper;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetEffectiveStaff

    a = FoscStaff({ |i| FoscNote(60 + i, [1, 4]) } ! 4);
    a[0].prGetEffectiveStaff == a;
    -------------------------------------------------------------------------------------------------------- */
    prGetEffectiveStaff {
        var staffChange, effectiveStaff;
        staffChange = this.prGetEffective(FoscStaffChange);
        if (staffChange.notNil) {
            effectiveStaff = staffChange.staff;
        } {
            effectiveStaff = this.prGetParentage.firstInstanceOf(FoscStaff);
        };
        ^effectiveStaff;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    a = FoscContainer([FoscNote(60, [1, 4])]);
    a.prGetFormatPieces;

    a = FoscContainer([FoscNote(60, [1, 4])]);
    a.prFormatComponent(true);

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscNote(60, 1/4)]);
    a.prFormatComponent(true);
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        var result;
        result = this.prFormatComponent(pieces: true);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetIndicator
    
    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.prGetIndicator.cs;

    x.attach(FoscArticulation('>'));
    x.prGetIndicator;

    x.prGetIndicator(FoscDynamic).cs;

    x.prGetIndicator(FoscMetronomeMark);
    -------------------------------------------------------------------------------------------------------- */
    prGetIndicator { |prototype, attributes, unwrap=true|
        var indicators;
        indicators = this.prGetIndicators(prototype, attributes, unwrap);
        case 
        { indicators.isEmpty } {
            throw("%:% no attached indicators found matching %."
                .format(this.species, thisMethod.name,prototype));
        }
        { indicators.size > 1 } {
            throw("%:% multiple attached indicators found matching %."
                .format(this.species, thisMethod.name,prototype));
        }
        { ^indicators[0] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetIndicators

    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.attach(FoscArticulation('>'));
    x.prGetIndicators;
    -------------------------------------------------------------------------------------------------------- */
    prGetIndicators { |prototype, attributes, unwrap=true|
        var result, prototypeObjects, prototypeClasses, indicator, newResult;

        result = [];
        prototype = prototype ? [Object];
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        prototypeObjects = [];
        prototypeClasses = [];
        prototype.do { |indicatorPrototype|
            if (prototype.any { |type| indicatorPrototype.isKindOf(Class) }) {
                prototypeClasses = prototypeClasses.add(indicatorPrototype);
            } {
                prototypeObjects = prototypeObjects.add(indicatorPrototype);
            };
        };

        wrappers.do { |wrapper|
            if (wrapper.annotation.isNil) {
                case
                { prototypeClasses.any { |type| wrapper.isKindOf(type) } } {
                    result = result.add(wrapper);
                }
                { prototypeObjects.any { |each| wrapper == each } } {
                    result = result.add(wrapper);
                }
                { wrapper.isKindOf(FoscWrapper) } {
                    if (prototypeClasses.any { |type| wrapper.indicator.isKindOf(type) }) {
                        result = result.add(wrapper);
                    } {
                        if (prototypeObjects.any { |each| wrapper.indicator == each }) {
                            result = result.add(wrapper);
                        };
                    };
                };
            };
        };

        if (attributes.notNil) {
            newResult = [];
            result.do { |wrapper|
                attributes.vars.keysValuesDo { |key, val|
                    block { |break|
                        if (wrapper.indicator.respondsTo('key') && { wrapper.indicator.key != val }) {
                            break.value;
                        };
                    };
                };
                newResult = newResult.add(wrapper);
            };
            result = newResult;
        };

        if (unwrap) { result = result.collect { |each| each.indicator } };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat

    a = FoscNote(60, 1/4);
    a.prGetLilypondFormat;

    a = FoscChord(#[60,64,67,72], 1/4);
    a.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        this.prUpdateNow(indicators: true);
        ^this.prFormatComponent;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetMarkup

    a = FoscNote(60, 1/4);
    a.attach(FoscMarkup("foo"));
    a.prGetMarkup;
    -------------------------------------------------------------------------------------------------------- */
    prGetMarkup { |direction|
        var markup;
        markup = this.prGetIndicators(FoscMarkup);
        case
        { direction == 'up' } { ^markup.select { |each| each.direction == 'up' } }
        { direction == 'down' } { ^markup.select { |each| each.direction == 'down' } };
        ^markup;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetParentage
    
    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    a[0].prGetParentage.components;
    -------------------------------------------------------------------------------------------------------- */
    prGetParentage { |graceNotes=false|
        ^FoscParentage(this, graceNotes);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTimespan

    a = FoscNote(60, 1/4);
    b = a.prGetTimespan;
    [b.startOffset.cs, b.stopOffset.cs].postln;
    -------------------------------------------------------------------------------------------------------- */
    prGetTimespan { |inSeconds=false|
        if (inSeconds) {
            this.prUpdateNow(offsetsInSeconds: true);
            if (startOffsetInSeconds.isNil) {
                throw("%:%: missing metronome mark.".format(this.species, thisMethod.name));
            };
            ^FoscTimespan(startOffsetInSeconds, stopOffsetInSeconds);
        } {
            this.prUpdateNow(offsets: true);
            ^timespan;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prHasEffectiveIndicator

    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.prHasEffectiveIndicator(FoscDynamic);
    -------------------------------------------------------------------------------------------------------- */
    prHasEffectiveIndicator { |prototype, attributes, command|
        var indicator;
        indicator = this.prGetEffective(prototype, attributes, command);
        ^indicator.notNil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prHasIndicator
    
    a = FoscNote(60, 1/4);
    a.attach(FoscDynamic('p'));
    a.prHasIndicator;
    a.prHasIndicator(FoscDynamic);
    a.prHasIndicator(FoscMetronomeMark);
    -------------------------------------------------------------------------------------------------------- */
    prHasIndicator { |prototype, attributes|
        var indicators;
        indicators = this.prGetIndicators(prototype, attributes);
        ^indicators.notEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prImmediatelyPrecedes
    
`   a = FoscNote(60, 1/4);
    b = FoscNote(60, 1/4);
    c = FoscNote(60, 1/4);
    FoscVoice([a, b, c]);

    a.prImmediatelyPrecedes(b);      // true
    b.prImmediatelyPrecedes(c);      // true
    c.prImmediatelyPrecedes(a);      // false
    -------------------------------------------------------------------------------------------------------- */
    prImmediatelyPrecedes { |component|
        var successors, current, sibling;
        successors = [];
        current = this;
        block { |break|
            while { current.notNil } {
                sibling = current.prSibling(1);
                //!!!TODO: sibling = current.prSiblingWithGraces(1);
                if (sibling.isNil) {
                    current = current.parent;
                } {
                    successors = sibling.prGetDescendantsStartingWith;
                    break.value;
                };
            };
        };
        ^successors.includes(component);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMoveIndicators

    a = FoscNote(60, 1/4);
    a.attach(FoscDynamic('p'));
    b = FoscNote(60, 1/4);

    a.prHasIndicator(FoscDynamic);
    b.prHasIndicator(FoscDynamic);

    a.prMoveIndicators(b);

    a.prHasIndicator(FoscDynamic);
    b.prHasIndicator(FoscDynamic);
    -------------------------------------------------------------------------------------------------------- */
    prMoveIndicators { |recipientComponent|
        wrappers.do { |wrapper|
            this.detach(wrapper);
            recipientComponent.attach(wrapper);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRemoveFromParent

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    a.parent;
    b.components;

    a.prRemoveFromParent;
    a.parent;
    b.components;
    -------------------------------------------------------------------------------------------------------- */
    prRemoveFromParent {
        this.prUpdateLater(offsets: true);
        this.prGetParentage[1..].do { |component|
            if (component.isKindOf(FoscContext)) {
                component.dependentWrappers.do { |wrapper|
                    if (wrapper.component == this) {
                        component.dependentWrappers.remove(wrapper);
                    };
                };
            };
        };
        if (parent.notNil) { parent.components.remove(this) };
        parent = nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRemoveNamedChildrenFromParentage
    -------------------------------------------------------------------------------------------------------- */
    prRemoveNamedChildrenFromParentage { |nameDictionary|
        var namedChildren;
        if (parent.notNil && nameDictionary.notNil) {
            this.prGetParentage[1..].do { |parent|
                namedChildren = parent.namedChildren;
                nameDictionary.keys.do { |name|
                    nameDictionary[name].do { |component| namedChildren[name].remove(component) };
                    if (namedChildren[name].isEmpty) { namedChildren.removeAt(name) };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRestoreNamedChildrenToParentage
    -------------------------------------------------------------------------------------------------------- */
    prRestoreNamedChildrenToParentage { |nameDictionary|
        var namedChildren;
        if (nameDictionary.notEmpty) {
            this.prGetParentage[1..].do { |parent|
                namedChildren = parent.namedChildren;
                nameDictionary.keys.do { |name|
                    if (namedChildren[name].notNil) {
                        namedChildren[name] = namedChildren[name].union(nameDictionary[name]);
                    } {
                        namedChildren[name] = nameDictionary[name].copy;
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetParent
    -------------------------------------------------------------------------------------------------------- */
    prSetParent { |newParent|
        var namedChildren;
        namedChildren = this.prCacheNamedChildren;
        this.prRemoveNamedChildrenFromParentage(namedChildren);
        this.prRemoveFromParent;
        parent = newParent;
        this.prRestoreNamedChildrenToParentage(namedChildren);
        this.prUpdateLater(offsets: true);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSibling
    
    a = FoscNote(60, 1/4);
    b = FoscNote(60, 1/4);
    c = FoscNote(60, 1/4);
    FoscVoice([a, b, c]);
    
    a.prSibling(1) === b;       // true
    b.prSibling(-1) === a;      // true
    a.prSibling(-1) === b;      // false
    -------------------------------------------------------------------------------------------------------- */
    prSibling { |n|
        var getSibling, sibling, componentParent, index;
        
        assert(
            #[-1, 1].includes(n),
            "%:%: n must be -1 or 1: %.".format(this.species, thisMethod.name, n);
        );

        getSibling = { |component, n|
            componentParent = component.parent;
            case
            { componentParent.isNil } { nil }
            { componentParent.isSimultaneous } { nil }
            {
                index = componentParent.indexOf(component) + n;
                if ((0 <= index) && { index < componentParent.size }) { componentParent[index] } { nil };
            };
        };

        this.prGetParentage.do { |parent|
            //!!! OLD: sibling = parent.prSibling2(n.sign);
            sibling = getSibling.(parent, n.sign);
            if (sibling.notNil) { ^sibling };
        };
        
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prTagStrings

    FoscLilypondFormatManager.tag(["foo", "bar"], tag: 'CLAR');
    -------------------------------------------------------------------------------------------------------- */
    prTagStrings { |strings|
        ^FoscLilypondFormatManager.tag(strings, tag: tag);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateLater
    -------------------------------------------------------------------------------------------------------- */
    prUpdateLater { |offsets=false, offsetsInSeconds=false|
        if (offsets.not && offsetsInSeconds.not) {
            throw("%:%: either offsets or offsetsInSeconds must be true."
                .format(this.species, thisMethod.name))
        };
        this.prGetParentage.do { |component|
            case
            { offsets } {
                component.instVarPut('offsetsAreCurrent', false);
            } {
                component.instVarPut('offsetsInSecondsAreCurrent', false);
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateMeasureNumbers
    -------------------------------------------------------------------------------------------------------- */
    prUpdateMeasureNumbers {
        FoscUpdateManager().prUpdateMeasureNumbers(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateNow
    -------------------------------------------------------------------------------------------------------- */
    prUpdateNow { |offsets=false, offsetsInSeconds=false, indicators=false|
        ^FoscUpdateManager().prUpdateNow(this, offsets, offsetsInSeconds, indicators);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TO BE DEPRECATED
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // prSibling2 { |n|
    //     var sibling, index;
    //     if (parent.isNil) { ^nil };
    //     if (parent.isSimultaneous) { ^nil };
    //     index = parent.indexOf(this) + n;
    //     if ((0 <= index) && { index < parent.size }) { ^parent[index] } { ^nil };
    // }
}
