/* ------------------------------------------------------------------------------------------------------------
• FoscContainer

An iterable container of components.
------------------------------------------------------------------------------------------------------------ */
FoscContainer : FoscComponent {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <components, <formatter, <name, <namedChildren, <isSimultaneous;
    *new { |components, isSimultaneous, name|
        components = components ? [];
        ^super.new.initFoscContainer(components, isSimultaneous, name);
    }
    initFoscContainer { |argComponents, argIsSimultaneous, argName|
        namedChildren = ();
        isSimultaneous = argIsSimultaneous ? false;
        this.prInitializeComponents(argComponents);
        if (argName.notNil) { name = argName.asSymbol };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NEW: ITERATION METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • doStaves
    -------------------------------------------------------------------------------------------------------- */
    doStaves { |function|
        this.doComponents(function, FoscStaff);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doStaffGroups
    -------------------------------------------------------------------------------------------------------- */
    doStaffGroups { |function|
        this.doComponents(function, FoscStaffGroup);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doTuplets
    -------------------------------------------------------------------------------------------------------- */
    doTuplets { |function|
        this.doComponents(function, FoscTuplet);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doVoices
    -------------------------------------------------------------------------------------------------------- */
    doVoices { |function|
        this.doComponents(function, FoscVoice);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • components

    Get components in container.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • eventLists

    a = FoscStaff(FoscLeafMaker().((60..63), [1/32]));
    b = FoscStaff(FoscLeafMaker().((67..70), [1/32]));
    c = FoscScore([a, b]);
    c.eventLists.do { |each| each.printAll; Post.nl };
    -------------------------------------------------------------------------------------------------------- */
    eventLists {
        var lists, recurse, eventList;

        if (this.isSimultaneous.not) {
            ^throw("'eventLists' not implemented for non-simultaneous containers. Use 'eventList'.");
        };

        lists = [];

        recurse = { |music|
            if (music.isSimultaneous) {
                music.do { |each| recurse.(each) };
            } {
                eventList = music.eventList;
                if (eventList.notEmpty) { lists = lists.add(music.eventList) };
            };
        };

        recurse.(this);

        ^lists;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSimultaneous

    Is true when container is simultaneous.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isSimultaneous_

    Set 'isSimultaneous' flag.
    -------------------------------------------------------------------------------------------------------- */
    isSimultaneous_ { |bool|
        if (bool.isNil) { ^this };
        
        //assert(bool.isKindOf(Boolean));
        
        if (bool) {
            this.do { |component|
                if (component.isKindOf(FoscContainer).not) {
                    ^throw("%:%: simultaneous containers must only contain other containers: %"
                        .format(this.species, thisMethod.name, component));
                };
            };
        };
        
        isSimultaneous = bool;
        this.prUpdateLater(offsets: true);
    }
    /* --------------------------------------------------------------------------------------------------------
    • name

    Get name of container.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • name_

    Set name of container.
    -------------------------------------------------------------------------------------------------------- */
    name_ { |name|
        this.instVarPut('name', name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • pattern

    
    • Example 1

    a = FoscStaff(FoscLeafMaker().((60..63), [1/32]));
    p = a.pattern;
    p.play;


    • Example 2

    a = FoscStaff(FoscLeafMaker().((60..63), [1/32]));
    b = FoscStaff(FoscLeafMaker().((67..70), [1/32]));
    c = FoscScore([a, b]);
    p = c.pattern;
    p.play;
    -------------------------------------------------------------------------------------------------------- */
    pattern { |name|
        if (isSimultaneous) {
            ^Ppar(this.eventLists.collect { |eventList| Pseq(eventList) });

        } {
            ^Pseq(this.eventList);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • do

    Iterates components. Non-recursive.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    do { |func|
        components.do(func);
    }
    /* --------------------------------------------------------------------------------------------------------
    • iter

    Iterates container.

    Returns a Routine.
    

    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.iter;
    a.size.do { b.next.postln };
    -------------------------------------------------------------------------------------------------------- */
    iter {
        ^components.iter;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.isEmpty;

    a.prEjectContents;
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    isEmpty {
        ^components.isEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • lastIndex

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.lastIndex;
    -------------------------------------------------------------------------------------------------------- */
    lastIndex {
        ^components.lastIndex;
    }
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.notEmpty;

    a.prEjectContents;
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    notEmpty {
        ^components.notEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • reverseDo

    Iterates components in reverse. Non-recursive.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.reverseDo { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    reverseDo { |function|
        components.reverseDo(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • size
    
    Gets number of items in container.
    
    Returns integer.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.size;
    -------------------------------------------------------------------------------------------------------- */
    size {
        ^components.size;
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.storeArgs;
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[[], isSimultaneous, name];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • add

    Adds 'component' to container.


    • add a note to end of container

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.add(FoscNote(67, 1/4));
    a.show;

    • add a new container to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.add(FoscTuplet(2/3, [FoscNote(67, 1/4), FoscNote(69, 1/8)]));
    a.show;

    • add a selection to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.add(FoscLeafMaker().(#[67,69], [1/4]));
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    add { |component|        
        this.prSetItem(this.size, component);
    }
    /* --------------------------------------------------------------------------------------------------------
    • addAll

    Adds all 'components' to container.


    • add notes to end of container

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.addAll([FoscNote(67, 1/4), FoscNote(69, 1/4)]);
    a.show;

    • add tuplet containers to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = FoscTuplet(2/3, [FoscNote(67, 1/4), FoscNote(69, 1/8)]);
    c = FoscTuplet(4/5, [FoscNote(71, 1/16), FoscNote(72, 1/4)]);
    a.addAll([b, c]);
    a.show;

    • add selections to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = FoscLeafMaker().(#[67,69], [1/4]);
    c = FoscLeafMaker().(#[71,72], [1/4]);
    a.addAll([b, c]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    addAll { |components|
        this.prSetItem(this.size, FoscSelection(components));
    }
    /* --------------------------------------------------------------------------------------------------------
    • at

    Gets item at 'index' in container. Traverses top-level items only.
    
    Returns component or selection.    


    • Example 1

    Get by index. Returns component.
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[2];


    • Example 2

    Get by indices. Returns selection.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = a[0..2];
    b.items;
    -------------------------------------------------------------------------------------------------------- */
    at { |index|
        ^this.prGetItem(index);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copySeries

    Gets item at indices in container. Traverses top-level items only.
    
    Returns component or selection.    

    • Example 1
    
    a = FoscVoice();
    a.addAll({ |i| FoscNote(60 + i, 1/4) } ! 8);
    a[1..2].do { |each| each.writtenPitch.str.postln  };
    a[1..].do { |each| each.writtenPitch.str.postln  };
    a[..2].do { |each| each.writtenPitch.str.postln  };
    a[1, 3 ..].do { |each| each.writtenPitch.str.postln  };
    a[1, 3 .. 5].do { |each| each.writtenPitch.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    copySeries { |first, second, last|
        if (first.isNil) { first = 0 };
        if (second.isNil) { second = first + 1 };
        if (last.isNil) { last = try { components.lastIndex } { nil } };
        
        if ([first, second, last].every { |each| each.notNil }) {
            ^this.prGetItem((first, second..last));
        } {
            ^FoscSelection([]);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • includes

    Is true when expr appears in container. Otherwise false.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    includes { |item|
        if (item.isKindOf(String) || { item.isKindOf (Symbol) }) {
            ^namedChildren.keys.includes(item.asSymbol);
        } {
            components.do { |each| if (each == item) { ^true } };            
        };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • includesAny
    -------------------------------------------------------------------------------------------------------- */
    includesAny { |items|
        items.do { |item| if (this.includes(item)) { ^true } };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • indexOf

    Returns index of 'component' in container.

    Returns nonnegative integer.


    • Example 1

    a = FoscStaff([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.leafAt(1);
    a.indexOf(b);
    -------------------------------------------------------------------------------------------------------- */
    indexOf { |component|
        components.do { |each, index| if (component == each) { ^index } };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • insert

    Inserts 'component' at 'index' in container.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.insert(1, FoscNote(72, 1/4));
    a.show;


    • Example 2

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.insert(1, [FoscNote(72, 1/4), FoscNote(74, 1/4)]);
    a.show;


    • Example 3

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.insert(1, FoscTuplet(2/3, [FoscNote(72, 1/4), FoscNote(74, 1/8)]));
    a.show;


    • Example 4

    Grow container if index is greater than size.

    a = FoscStaff([FoscNote(60, 1/4)]);
    a.insert(1, FoscNote(61, 1.4));
    a.components;
    -------------------------------------------------------------------------------------------------------- */
    insert { |index, component|
        if (index.isInteger.not || { index < 0 }) {
            ^throw("%:%: index must be a non-negative integer: %."
                .format(this.species, thisMethod.name, index));
        };

        this.prSetItem((index..index), component);
    }
    /* --------------------------------------------------------------------------------------------------------
    • leafAt

    Gets leaf at 'index' in container.

    
    • Example 1

    Get leaf at index 1.

    a = FoscStaff([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;


    • Example 2

    Get pitched leaf at index 1.

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0, pitched: true).str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • put

    Puts 'component' at 'index' in container.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[1] = FoscNote(72, 1/4);
    a.show;


    • Example 2

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[1] = [FoscNote(72, 1/4), FoscNote(74, 1/4)];
    a.show;


    • Example 3

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[1] = FoscTuplet(2/3, [FoscNote(72, 1/4), FoscNote(74, 1/8)]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    put { |index, component|
        if (index.isInteger.not || { index < 0 }) {
            ^throw("%:%: index must be a non-negative integer: %."
                .format(this.species, thisMethod.name, index));
        };
        this.prSetItem(index, component);
    }
    /* --------------------------------------------------------------------------------------------------------
    • remove

    Remove 'component' from container.

    Return removed component.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = a.leafAt(1);
    a.remove(b);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    remove { |component|
        var index;
        
        index = components.indexOf(component);
        
        if (index.isNil) {
            ^throw("%:%: component not found in container.".format(this.species, thisMethod.name));
        };
        
        ^this.prDelItem(index);
    }
    /* --------------------------------------------------------------------------------------------------------
    • removeAt

    Remove component at 'index' in container.

    Return removed component.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.removeAt(1);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    removeAt { |index|
        var component;
        
        component = components[index];
        
        if (component.isNil) {
            ^throw("%:%: no component found at index: %.".format(this.species, thisMethod.name, index));
        };
        
        ^this.prDelItem(index);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAllAreOrphanComponents

    x = FoscNote(60, 1);
    x.prGetParentage.isOrphan;

    FoscContainer([x = FoscNote(60, 1)]);
    x.prGetParentage.isOrphan;
    -------------------------------------------------------------------------------------------------------- */
    prAllAreOrphanComponents { |expr|
        expr.do { |component|
            if (component.isKindOf(FoscComponent).not) { ^false };
            if (component.prGetParentage.isOrphan.not) { ^false };
        };
        
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCopyWithChildren

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.prCopyWithChildren;
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    prCopyWithChildren {
        var newContainer, newComponent;
        
        newContainer = this.copy;
        
        this.do { |component|
            if (component.isKindOf(FoscContainer)) {
                newComponent = component.prCopyWithChildren;
            } {
                newComponent = component.copy;
            };
            newContainer.add(newComponent);
        };
        
        ^newContainer;  
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDelItem

    Deletes components(s) at 'index' in container.

    Returns components.
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prDelItem(2);
    a.show;

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prDelItem([1,3]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prDelItem { |index|
        var components;
        
        components = this[index];
        if (components.isKindOf(FoscSelection).not) { components = FoscSelection([components]) };
        components.prSetParents(nil);
        
        ^components;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prEjectContents

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.prEjectContents;
    b.items;
    a.components;


    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = a[0..2];
    b.items;
    -------------------------------------------------------------------------------------------------------- */
    prEjectContents {
        var contents;
        
        if (this.prGetParentage.parent.notNil) {
            ^throw("%:%: can't eject contents of in-score container.".format(this.species, thisMethod.name));
        };

        contents = this[0..];
        contents.do { |component| component.prSetParent(nil) };
        components = [];
        
        ^contents;
    } 
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatAfterSlot { |bundle|
        var result;
        
        result = [];
        result = result.add(['commands', bundle.after.commands]);
        result = result.add(['comments', bundle.after.comments]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatBeforeSlot { |bundle|
        var result;
        
        result = [];
        result = result.add(['comments', bundle.before.comments]);
        result = result.add(['commands', bundle.before.commands]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatCloseBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatCloseBracketsSlot { |bundle|
        var result, bracketsClose;
        
        result = [];
        bracketsClose = if (isSimultaneous) { [">>"] } { ["}"] };
        result = result.add([['closeBrackets', ''], bracketsClose]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatClosingSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatClosingSlot { |bundle|
        var result;
        
        result = [];
        result = result.add(['grobReverts', bundle.grobReverts]);
        result = result.add(['commands', bundle.closing.commands]);
        result = result.add(['comments', bundle.closing.comments]);
        
        ^this.prFormatSlotContributionsWithIndent(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContentPieces

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prFormatContentPieces;
    -------------------------------------------------------------------------------------------------------- */
    prFormatContentPieces {
        var indent, result;
        
        indent = FoscLilyPondFormatManager.indent;
        result = [];
        
        components.do { |component|
            result = result.addAll(component.format.split(Char.nl));
        };
        
        result = result.collect { |each| indent ++ each };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContentsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatContentsSlot { |bundle|
        var result;
        
        result = [];
        result = result.add([['contents', 'prGetContents'], this.prFormatContentPieces]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        var result, bracketsOpen;
        
        result = [];
        bracketsOpen = if (isSimultaneous) { ["<<"] } { ["{"] };
        result = result.add([['openBrackets', ''], bracketsOpen]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpeningSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpeningSlot { |bundle|
        var result;

        result = [];
        result = result.add(['comments', bundle.opening.comments]);
        result = result.add(['commands', bundle.opening.commands]);
        result = result.add(['grobOverrides', bundle.grobOverrides]);
        result = result.add(['contextSettings', bundle.contextSettings]);
        
        ^this.prFormatSlotContributionsWithIndent(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatSlotContributionsWithIndent
    -------------------------------------------------------------------------------------------------------- */
    prFormatSlotContributionsWithIndent { |slot|
        var indent, result, contributor, contributions;
        
        indent = FoscLilyPondFormatManager.indent;
        result = [];
        
        slot.do { |each|
            # contributor, contributions = each;
            result = result.add([contributor, contributions.collect { |each| indent ++ each }]);
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetAbbreviatedStringFormat

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetAbbreviatedStringFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetAbbreviatedStringFormat {
        var summary, openBracketString, closeBracketString, localName, result;
        
        summary = if (0 < this.size) { this.size.asString } { "" };    
        
        if (isSimultaneous) {
            openBracketString = "<<";
            closeBracketString = ">>";
        }  {
            openBracketString = "{";
            closeBracketString = "}";
        };   
        
        localName = if (name.notNil) { "-\"%\"" } { "" };
        
        if (this.respondsTo('lilypondType') && { this.lilypondType.notNil }) {
            result = "<%%%%%>"
                .format(this.lilypondType, localName, openBracketString, summary, closeBracketString);
        } {
            result = "<%%%%>".format(localName, openBracketString, summary, closeBracketString);
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetCompactRepresentation;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        if (components.isEmpty) { ^"{ }" };
        ^"{ % }".format(this.prGetContentsSummary);
    }
    /* --------------------------------------------------------------------------------------------------------
    •  prGetContentsDuration

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetContentsDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    prGetContentsDuration {
        var duration;
        
        if (this.isSimultaneous) {
            ^components.collect { |each| each.prGetPreprolatedDuration }.maxItem;
        } {
            duration = FoscDuration(0);
            components.do { |each| duration = duration + each.prGetPreprolatedDuration };
            ^duration;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetContentsSummary

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetContentsSummary;
    -------------------------------------------------------------------------------------------------------- */
    prGetContentsSummary {
        var result;
        
        if (0 < this.size) {
            result = [];
        
            components.do { |each|
                case
                {
                    each.respondsTo('prGetCompactRepresentationWithTie')
                    && { each.prGetCompactRepresentationWithTie.notNil }
                } {
                    result = result.add(each.prGetCompactRepresentationWithTie);
                }
                {
                    each.respondsTo('prGetCompactRepresentation')
                    && { each.prGetCompactRepresentation.notNil }
                } {
                    result = result.add(each.prGetCompactRepresentation);
                }
                {
                    result = result.add(each.str);
                };
            };

            ^result.join(" ");
        } {
            ^"";
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDurationInSeconds

    a = FoscScore([FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]))]);
    a.prGetDurationInSeconds.asFloat;

    a = FoscScore([FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]))]);
    a.leafAt(0).attach(FoscMetronomeMark([1,4], 72));
    a.prGetDurationInSeconds.asFloat;
    -------------------------------------------------------------------------------------------------------- */
    prGetDurationInSeconds {
        var duration;
        
        if (this.isSimultaneous) {
            duration = components.collect { |each| each.prGetDuration(inSeconds: true) }.maxItem;
            ^duration;
        } {
            duration = FoscDuration(0);
        
            this.doLeaves { |leaf|
                duration = duration + leaf.prGetDuration(inSeconds: true);
            };
        
            ^duration;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetItem

    Gets item at index in container. Traverses top-level items only.
    
    Returns component or selection.
    -------------------------------------------------------------------------------------------------------- */
    prGetItem { |index|
        case
        { index.isInteger } {
            ^components[index];
        }
        { index.isSequenceableCollection } {
            if (index.every { |each| each.notNil }) {
                ^FoscSelection(components.prGetItem(index));
            } {
                ^FoscSelection([]);      
            };
        }
        { [Symbol, String].includes(index.species) } {
            if (namedChildren.keys.includes(index).not) {
                ^nil;
                //^throw("%:%: can't find component named %.".format(this.species, thisMethod.name, index));
            };
            
            if (namedChildren[index].size > 1) {
                ^throw("%:%: multiple components named %.".format(this.species, thisMethod.name, index));
            };

            ^namedChildren[index][0];
        };

        ^throw("%:%: can't get item at index: %.".format(this.species, thisMethod.name, index))
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetPreprolatedDuration

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetPreprolatedDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    prGetPreprolatedDuration {
        ^this.prGetContentsDuration;
    }
    /* -------------------------------------------------------------------------------------------------------
    • prInitializeComponents
    -------------------------------------------------------------------------------------------------------- */
    prInitializeComponents { |components|
        var localComponents, parent, start, stop;
        
        if (components.isSequenceableCollection || { components.isKindOf(FoscSelection) }) {
            localComponents = [];
        
            components.do { |item|
                if (item.isKindOf(FoscSelection)) {
                    localComponents = localComponents.addAll(item.flat.items);
                } {
                    localComponents = localComponents.add(item);
                };
            };
        
            components = localComponents;
        
            components.do { |component|
                if (component.isKindOf(FoscComponent).not) {
                    ^throw("%:new: must be a FoscComponent: %".format(this.species, component));
                };
            };
        };
        
        if (this.prAllAreOrphanComponents(components)) {
            this.instVarPut('components', components);
            FoscSelection(components).prSetParents(this);
        };        
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIsOneOfMyFirstLeaves
    -------------------------------------------------------------------------------------------------------- */
    prIsOneOfMyFirstLeaves { |leaf|
        ^this.prGetDescendantsStartingWith.includes(leaf);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIsOneOfMyLastLeaves
    -------------------------------------------------------------------------------------------------------- */
    prIsOneOfMyLastLeaves { |leaf|
        ^this.prGetDescendantsStoppingWith.includes(leaf);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateBottomUp
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.prIterateBottomUp.do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    prIterateBottomUp {
        var routine, recurse;
       
        recurse = { |node|
            routine = Routine { 
                if (node.isKindOf(FoscContainer)) {
                    node.do { |each|
                        recurse.(each).do { |elem| elem.yield };
                    };
                };
                node.yield;
            };
        };
       
        recurse.(this);
        
        ^routine;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateTopDown

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.prIterateTopDown.do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    prIterateTopDown {
        var routine, recurse;
        
        recurse = { |node|
            routine = Routine { 
                node.yield;
                if (node.isKindOf(FoscContainer)) {
                    node.do { |each|
                        recurse.(each).do { |elem| elem.yield };
                    };
                };
            };
        };
        
        recurse.(this);
        
        ^routine;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateTopmost

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.prIterateTopmost.do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    prIterateTopmost {
        var logicalTie;
        
        ^Routine {
            this.do { |component|
                if (component.isKindOf(FoscLeaf)) {
                    logicalTie = component.prGetLogicalTie;
                    if (logicalTie.isTrivial || { logicalTie.last === component }) {
                        logicalTie.yield;
                    }
                } {
                    assert(component.isKindOf(FoscContainer));
                    component.yield;
                };
            }
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prScale

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    a.prScale(2);
    a.show;

    b = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a = FoscStaff([b]);
    a.prScale(1.25);
    a.show;

    !!!TODO: BUG
    b = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a = FoscStaff([b]);
    a.prScale(1/3);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prScale { |multiplier|
        this.prScaleContents(multiplier);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prScaleContents
    -------------------------------------------------------------------------------------------------------- */
    prScaleContents { |multiplier|
        all(this.prIterateTopmost).do { |each| each.prScale(multiplier) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetItem

    • add note to end of container

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(a.size, FoscNote(67, 1/4));
    a.show;

    • add tuplet to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(a.size, FoscTuplet(2/3, [FoscNote(67, 1/4), FoscNote(69, 1/8)]));
    a.show;

    • add selection to end of container
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(a.size, FoscLeafMaker().(#[67,69], [1/4]));
    a.show;

    • replace last two items in container with note

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem((2..4), FoscNote(67, 1/4));
    a.show;

    • replace last two items in container with tuplet
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem((2..4), FoscTuplet(2/3, [FoscNote(67, 1/4), FoscNote(69, 1/8)]));
    a.show;

    • replace last two items in container with selection
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem((2..4), FoscLeafMaker().(#[67,69], [1/4]));
    a.show;

    • insert note in container at index

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(#[1,1], FoscNote(67, 1/4));
    a.show;

    • insert tuplet in container at index
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(#[1,1], FoscTuplet(2/3, [FoscNote(67, 1/4), FoscNote(69, 1/8)]));
    a.show;

    • insert selection in container at index
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(#[1,1], FoscLeafMaker().(#[67,69], [1/4]));
    a.show;


    • grow container if index is greater than container size
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prSetItem(#[4,4], FoscLeafMaker().(#[67,69], [1/4]));
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prSetItem { |index, object|
        var componentIndicators, wrappers, type, newObject, start, stop, oldComponents;

        componentIndicators = []; 
        
        FoscIteration(object).components.do { |component|
            //wrappers = component.prGetIndicators(unwrap: false);
            wrappers = FoscInspection(component).wrappers;
            componentIndicators = componentIndicators.addAll(wrappers);
        };
        
        if (object.isSequenceableCollection.not) { object = [object] };
        if (index.isInteger) { index = [index, index + 1] };
        
        type = [FoscComponent, FoscSelection];
        assert(object.every { |item| type.any { |type| item.isKindOf(type) } });
        newObject = [];   
        
        object.do { |each|
            if (each.isKindOf(FoscSelection)) {
                newObject = newObject.addAll(each.flat.items);
            } {
                newObject = newObject.add(each);
            };
        };
        
        object = newObject;
        
        assert(object.every { |each| each.isKindOf(FoscComponent) });
        
        if (object.any { |each| each.isKindOf(FoscGraceContainer) }) {
            ^throw("%:%: grace container must be attached to note or chord."
                .format(this.species, thisMethod.name));
        };
        
        if (this.prCheckForCycles(object)) {
            ^throw("%:%: attempted to induce cycles.".format(this.species, thisMethod.name));
        };
        
        start = index.first;
        stop = index.last;
        components = components.prSetItem((start..stop), object);
        object.do { |component| component.prSetParent(this) };
        
        componentIndicators.do { |indicator|
            if (indicator.respondsTo('prUpdateEffectiveContext')) {
                indicator.prUpdateEffectiveContext;
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSplitAtIndex

    Splits container to the left of index i.

    Preserves tuplet multiplier when container is a tuplet.

    Resizes resizable containers.

    Returns split parts.

    a = FoscVoice({ FoscNote(60, 1/4) } ! 8);
    b = a.prSplitAtIndex(3);
    b.do { |each| each.components.postln };
    -------------------------------------------------------------------------------------------------------- */
    prSplitAtIndex { |index|
        var leftComponents, rightComponents, multiplier, left, right, halves, nonEmptyHalves, selection;
        var parent, start, stop;

        leftComponents = this[..(index - 1)];
        rightComponents = this[index..];

        if (this.isKindOf(FoscTuplet)) {
            multiplier = this.multiplier;
            left = this.species.new(multiplier, []);
            mutate(leftComponents).wrap(left);
            right = this.species.new(multiplier, []);
            mutate(rightComponents).wrap(right);
        } {
            left = this.copy;
            mutate(leftComponents).wrap(left);
            right = this.copy;
            mutate(rightComponents).wrap(right);
        };
        
        halves = [left, right];
        nonEmptyHalves = halves.select { |half| half.components.notEmpty };
        selection = FoscSelection(this);
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        
        if (parent.notNil) {
            parent.components.prSetItem((start..(stop + 1)), nonEmptyHalves);
            nonEmptyHalves.do { |part| part.prSetParent(parent) };
        } {
            left.prSetParent(nil);
            right.prSetParent(nil);
        };
        
        ^halves;
    }

    prSplitAtIndex_OLD { |index, fractureSpanners=false|
        var leftMusic, rightMusic, timeSignature, denominator, leftDuration, leftPair, leftTimeSignature;
        var left, rightDuration, rightPair, rightTimeSignature, right, multiplier, halves, nonEmptyHalves;
        var selection, parent, start, stop, leftIndex;
        leftMusic = this[..(index - 1)];
        rightMusic = this[index..];
        // instantiate new left and right containers
        case
        { this.isKindOf(FoscMeasure) } {
            timeSignature = this.prGetEffective(FoscTimeSignature);
            denominator = timeSignature.denominator;
            leftDuration = leftMusic.collect { |each| each.prGetDuration }.sum;
            leftPair = FoscNonreducedFraction(leftDuration);
            leftPair = leftPair.withMultiplierOfDenominator(denominator);
            leftTimeSignature = FoscTimeSignature(leftPair);
            left = this.species.new(leftTimeSignature, []);
            mutate(leftMusic).wrap(left);
            left.implicitScaling_(this.implicitScaling);
            rightDuration = rightMusic.collect { |each| each.prGetDuration }.sum;
            rightPair = FoscNonreducedFraction(rightDuration);
            rightPair = leftPair.withMultiplierOfDenominator(denominator);
            rightTimeSignature = FoscTimeSignature(rightPair);
            right = this.species.new(rightTimeSignature, []);
            mutate(rightMusic).wrap(right);
            right.implicitScaling_(this.implicitScaling);
        }
        { this.isKindOf(FoscTuplet) } {
            multiplier = this.multiplier;
            left = this.species.new(multiplier, []);
            mutate(leftMusic).wrap(left);
            right = this.species.new(multiplier, []);
            mutate(rightMusic).wrap(right);
        } {
            left = this.prCopyWithIndicatorsButWithoutChildrenOrSpanners;
            mutate(leftMusic).wrap(left);
            right = this.prCopyWithIndicatorsButWithoutChildrenOrSpanners;
            mutate(rightMusic).wrap(right);
        };
        halves = [left, right];
        nonEmptyHalves = halves.select { |half| half.components.notEmpty };
        // give my attached spanners to my children
        this.prMoveSpannersToChildren;
        // incorporate left and right parents in score if possible
        selection = this.select;
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        if (parent.notNil) {
            //??? parent.components.prSetItem((start..stop), nonEmptyHalves);
            parent.components.prSetItem((start..(stop + 1)), nonEmptyHalves);
            nonEmptyHalves.do { |part| part.prSetParent(parent) };
        } {
            left.prSetParent(nil);
            right.prSetParent(nil);
        };
        // fracture spanners if requested
        if (fractureSpanners) {
            left.prSpanners.do { |spanner|
                leftIndex = spanner.prIndexOf(left);
                spanner.prFracture(leftIndex, direction: 'right');
            };
        };
        // return new left and right containers
        ^halves;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSplitByDuration


    • Example 1

    a = FoscVoice({ FoscNote(60, 3/8) } ! 4);
    a.prSplitByDuration(3/4);


    • Example 2

    a = FoscVoice([FoscNote(60, 4/4)]);
    a.prSplitByDuration(3/4, tieSplitNotes: false);


    • Example 3

    a = FoscVoice([FoscNote(60, [4, 4])]);
    b = a.prSplitByDuration([3, 4], tieSplitNotes: true);
    b.do { |each| Post.nl; each[0].components.collect { |elem| elem.format }.postln };
    b.show;


    • Example 4

    a = FoscVoice({ |i| FoscNote(60 + i, [3, 8]) } ! 4);
    b = a.prSplitByDuration([3, 16]);
    b.do { |each| Post.nl; each[0].components.collect { |elem| elem.format }.postln };
    a.show;


   
   
    -------------------------------------------------------------------------------------------------------- */
    prSplitByDuration { |duration, tieSplitNotes=true, repeatTies=false|
        var timespan, globalSplitPoint, crossOffset, durationCrossingDescendants, startOffset, stopOffset;
        var bottom, didSplitLeaf, splitPointInBottom, newLeaves, leftList, rightList, left, right;
        var leafRightOfSplit, leafLeftOfSplit, durationCrossingContainers, component, parent, index;
        var highestLevelComponentRightOfSplit, previous, leavesAroundSplit, selection;

        // if self.is_simultaneous:
        //     return self._split_simultaneous_by_duration(
        //         duration=duration,
        //         tie_split_notes=tie_split_notes,
        //         repeat_ties=repeat_ties,
        //         )
        if (isSimultaneous) {
            ^this.prSplitSimultaneousByDuration(duration, tieSplitNotes, repeatTies);
        };
        \a.postln;
        // duration = Duration(duration)
        // assert 0 <= duration, repr(duration)
        // if duration == 0:
        //     return [], self
        // # get split point score offset
        // timespan = inspect(self).timespan()
        // global_split_point = timespan.start_offset + duration 
        duration = FoscDuration(duration);
        assert(0 <= duration);
        if (duration == 0) { ^[[], this] };
        timespan = FoscInspection(this).timespan;
        globalSplitPoint = timespan.startOffset + duration;
    
        \b.postln;
        // # get any duration-crossing descendents
        // cross_offset = timespan.start_offset + duration
        // duration_crossing_descendants = []
        // for descendant in inspect(self).descendants():
        //     timespan = inspect(descendant).timespan()
        //     start_offset = timespan.start_offset
        //     stop_offset = timespan.stop_offset
        //     if start_offset < cross_offset < stop_offset:
        //         duration_crossing_descendants.append(descendant)
        // # any duration-crossing leaf will be at end of list
        // bottom = duration_crossing_descendants[-1]
        // did_split_leaf = False
        crossOffset = timespan.startOffset + duration;
        durationCrossingDescendants = [];
        FoscInspection(this).descendants.do { |descendant|
            timespan = FoscInspection(descendant).timespan;
            startOffset = timespan.startOffset;
            stopOffset = timespan.stopOffset;
            if (startOffset < crossOffset && { crossOffset < stopOffset }) {
                durationCrossingDescendants = durationCrossingDescendants.add(descendant);  
            };
        };
        bottom = durationCrossingDescendants.last;
        didSplitLeaf = false;
      
        \c.postln;  
        // # if split point necessitates leaf split
        // if isinstance(bottom, Leaf):
        //     assert isinstance(bottom, Leaf)
        //     did_split_leaf = True
        //     timespan = inspect(bottom).timespan()
        //     start_offset = timespan.start_offset
        //     split_point_in_bottom = global_split_point - start_offset
        //     new_leaves = bottom._split_by_durations(
        //         [split_point_in_bottom],
        //         tie_split_notes=tie_split_notes,
        //         repeat_ties=repeat_ties,
        //         )
        //     for leaf in new_leaves:
        //         timespan = inspect(leaf).timespan()
        //         if timespan.stop_offset == global_split_point:
        //             leaf_left_of_split = leaf
        //         if timespan.start_offset == global_split_point:
        //             leaf_right_of_split = leaf
        //     duration_crossing_containers = duration_crossing_descendants[:-1]
        //     if not len(duration_crossing_containers):
        //         return left_list, right_list
        // # if split point falls between leaves
        // # then find leaf to immediate right of split point
        // # in order to start upward crawl through duration-crossing containers
        // else:
        //     duration_crossing_containers = duration_crossing_descendants[:]
        //     for leaf in iterate(bottom).leaves():
        //         timespan = inspect(leaf).timespan()
        //         if timespan.start_offset == global_split_point:
        //             leaf_right_of_split = leaf
        //             leaf_left_of_split = inspect(leaf).leaf(-1)
        //             break
        //     else:
        //         raise Exception('can not split empty container {bottom!r}.')
        if (bottom.isKindOf(FoscLeaf)) {
            /*
            a = FoscVoice([FoscNote(60, 4/4)]);
            a.prSplitByDuration(3/4, tieSplitNotes: false);
            */
            \c1.postln;
            didSplitLeaf = true;
            timespan = FoscInspection(bottom).timespan;
            startOffset = timespan.startOffset;
            splitPointInBottom = globalSplitPoint - startOffset;
            \c2.postln;
            newLeaves = bottom.prSplitByDurations(
                [splitPointInBottom],
                tieSplitNotes: tieSplitNotes,
                repeatTies: repeatTies
            );
            \c3.postln;
            newLeaves.do { |leaf|
                "leaf: ".post; leaf.postln;
                timespan = FoscInspection(leaf).timespan;
                "timespan: ".post; timespan.postln;
                [timespan.stopOffset, timespan.startOffset, globalSplitPoint].postln;
                if (timespan.stopOffset == globalSplitPoint) { leafLeftOfSplit = leaf };
                if (timespan.startOffset == globalSplitPoint) { leafRightOfSplit = leaf };
            };
            \c4.postln;
            durationCrossingContainers = durationCrossingDescendants.drop(-1);
            if (durationCrossingContainers.isEmpty) { ^[leftList, rightList] };
        } {
            \c5.postln;
            block { |break|
                durationCrossingContainers = durationCrossingDescendants.copy;
                FoscIteration(bottom).leaves.do { |leaf|
                    timespan = FoscInspection(leaf).timespan;
                    if (timespan.startOffset == globalSplitPoint) {
                        leafRightOfSplit = leaf;
                        leafLeftOfSplit = leaf.prLeafAt(-1);
                        break.value;
                    };
                };
                ^throw("%:%: can't split empty container: %.".format(this.species, thisMethod.name, bottom));
            };
        };
        

        \d.postln;
        // assert leaf_left_of_split is not None
        // assert leaf_right_of_split is not None
        // # find component to right of split
        // # that is also immediate child of last duration-crossing container
        // for component in inspect(leaf_right_of_split).parentage():
        //     parent = inspect(component).parentage().parent
        //     if parent is duration_crossing_containers[-1]:
        //         highest_level_component_right_of_split = component
        //         break
        // else:
        //     raise ValueError('should not be able to get here.')

        block { |break|
            FoscInspection(leafRightOfSplit).parentage.do { |localComponent|
                component = localComponent;
                parent = FoscInspection(component).parentage.parent;
                if (parent === durationCrossingContainers.last) {
                    highestLevelComponentRightOfSplit = component;
                    break.value;
                };
            };
            ^throw("%:%: should not be able to get here.".format(this.species, thisMethod.name));
        };
      


        \e.postln;
        // # crawl back up through duration-crossing containers and split each
        // previous = highest_level_component_right_of_split
        // for container in reversed(duration_crossing_containers):
        //     assert isinstance(container, Container)
        //     index = container.index(previous)
        //     left, right = container._split_at_index(index)
        //     previous = right
        previous = highestLevelComponentRightOfSplit;
        durationCrossingContainers.reverseDo { |container|
            assert(container.isKindOf(FoscContainer));
            index = container.indexOf(previous);
            # left, right = container.prSplitAtIndex(index);
            previous = right;
        }; 


        \f.postln;
        // # reapply tie here if crawl above killed tie applied to leaves
        // if did_split_leaf:
        //     if (
        //         tie_split_notes and
        //         isinstance(leaf_left_of_split, Note)
        //         ):
        //         if (
        //             inspect(leaf_left_of_split).parentage().root is
        //             inspect(leaf_right_of_split).parentage().root
        //             ):
        //             leaves_around_split = (
        //                 leaf_left_of_split,
        //                 leaf_right_of_split,
        //                 )
        //             selection = select(leaves_around_split)
        //             selection._attach_tie_to_leaves(repeat_ties=repeat_ties)
        if (didSplitLeaf) {
            if (
                tieSplitNotes
                && { leafLeftOfSplit.isKindOf(FoscNote) }
            ) {
                if (leafLeftOfSplit.prParentage.root === leafRightOfSplit.prParentage.root) {
                    leavesAroundSplit = [leafLeftOfSplit, leafRightOfSplit];
                    selection = FoscSelection(leavesAroundSplit);
                    selection.prAttachTieToLeaves(repeatTies: repeatTies);
                };
            };
        };
        \g.postln;

        // # return list-wrapped halves of container
        // return [left], [right]
        ^[[left], [right]];
    }
    prSplitByDuration_OLD { |duration, fractureSpanners=false, tieSplitNotes=true|
        var timespan, globalSplitPoint, crossOffset, durationCrossingDescendants, startOffset, stopOffset;
        var measures, measure, splitPointInMeasure, nonPowerOfTwoFactors, nonPowerOfTwoProduct;
        var bottom, didSplitLeaf, splitPointInBottom, leftList, rightList, left, right, leafRightOfSplit;
        var leafLeftOfSplit, durationCrossingContainers, agent, parentage, component, parent, index;
        var highestLevelComponentRightOfSplit, previous, leftLogicalTie, rightLogicalTie, leavesAroundSplit;
        var selection;
        
        duration = FoscDuration(duration);
        // assert 0 <= duration, repr(duration)
        if (duration == 0) { ^[[], this] };
        // get split point score offset
        timespan = FoscInspection(this).timespan;
        globalSplitPoint = timespan.startOffset + duration;
        // get any duration-crossing descendents
        crossOffset = timespan.startOffset + duration;
        durationCrossingDescendants = [];
        FoscInspection(this).descendants.do { |descendant|
            timespan = FoscInspection(descendant).timespan;
            startOffset = timespan.startOffset;
            stopOffset = timespan.stopOffset;
            if (startOffset < crossOffset && { crossOffset < stopOffset }) {
                durationCrossingDescendants = durationCrossingDescendants.add(descendant);  
            };
        };

        // get any duration-crossing measure descendents
        measures = durationCrossingDescendants.select { |each| each.isKindOf(FoscMeasure) };
        // if we must split a power-of-two measure at non-power-of-two split point then go ahead
        // and transform the power-of-two measure to non-power-of-two equivalent now
        // code that crawls and splits later on will be happier
        if (measures.size == 1) {
            measure = measures[0];
            timespan = FoscInspection(measure).timespan;
            startOffset = timespan.startOffset;
            splitPointInMeasure = globalSplitPoint - startOffset;
            if (measure.hasNonPowerOfTwoDenominator) {
                // pass
            } {
                if (splitPointInMeasure.denominator.isPowerOfTwo) {
                    nonPowerOfTwoFactors = FoscContainer.prRemovePowersOfTwo(splitPointInMeasure.denominator);
                    nonPowerOfTwoFactors = nonPowerOfTwoFactors.factors;
                    nonPowerOfTwoProduct = 1;
                    nonPowerOfTwoFactors.do { |nonPowerOfTwoFactor|
                        nonPowerOfTwoProduct = nonPowerOfTwoProduct * nonPowerOfTwoFactor;
                    };
                    measure.prScaleDenominator(nonPowerOfTwoProduct);
                    // rederive duration crossers with possibly new measure contents
                    timespan = FoscInspection(this).timespan;
                    crossOffset = timespan.startOffset + duration;
                    durationCrossingDescendants = [];
                    FoscInspection(this).descendants.do { |descendant|
                        startOffset = timespan.startOffset;
                        stopOffset = timespan.stopOffset;
                        if (startOffset < crossOffset && { crossOffset < stopOffset }) {
                            durationCrossingDescendants = durationCrossingDescendants.add(descendant);  
                        };
                    };
                };
            };
        } {
            if (measures.size > 1) {
                ^throw("%:%: measures can not nest.".format(this.species, thisMethod.name));
            };
        };
        // any duration-crossing leaf will be at end of list
        bottom = durationCrossingDescendants.last;
        didSplitLeaf = false;
        // if split point necessitates leaf split
        if (bottom.isKindOf(FoscLeaf)) {
            didSplitLeaf = true;
            timespan = FoscInspection(bottom).timespan;
            startOffset = timespan.startOffset;
            splitPointInBottom = globalSplitPoint - startOffset;
            # leftList, rightList = bottom.prSplitByDurations(
                [splitPointInBottom],
                fractureSpanners,
                tieSplitNotes
            );
            right = rightList[0];
            leafRightOfSplit = right;
            leafLeftOfSplit = leftList.last;
            durationCrossingContainers = durationCrossingDescendants.drop(-1);
            if (durationCrossingContainers.isEmpty) { ^[leftList, rightList] };
        } {
            // if split point falls between leaves then find leaf to immediate right of split point
            // in order to start upward crawl through duration-crossing containers
            block { |break|
                durationCrossingContainers = durationCrossingDescendants.copy;
                FoscIteration(bottom).leaves.do { |leaf|
                    timespan = FoscInspection(leaf).timespan;
                    if (timespan.startOffset == globalSplitPoint) {
                        leafRightOfSplit = leaf;
                        leafLeftOfSplit = FoscInspection(leaf).leafAt(-1);
                        break.value;
                    };
                    //!!!
                    // ^throw("%:%: can't split empty container: %."
                    //     .format(this.species, thisMethod.name, bottom));
                };
            };
        };
        // assert leaf_left_of_split is not None
        // assert leaf_right_of_split is not None
        // find component to right of split that is also immediate child of last duration-crossing container
        agent = FoscInspection(leafRightOfSplit);
        //!!!TODO: remove 'includeSelf'
        parentage = agent.parentage(includeSelf: true);
        block { |break|
            parentage.do { |localComponent|
                component = localComponent;
                parent = FoscInspection(component).parentage.parent;
                if (parent == durationCrossingContainers.last) {
                    highestLevelComponentRightOfSplit = component;
                    break.value;
                };
            };
            ^throw("%:%: should not be able to get here.".format(this.species, thisMethod.name));
        };
        // crawl back up through duration-crossing containers and fracture spanners if requested
        block { |break|
            if (fractureSpanners) {
                agent = FoscInspection(leafRightOfSplit);
                startOffset = agent.timespan.startOffset;
                agent.parentage.do { |parent|
                    timespan = FoscInspection(parent).timespan;
                    if (timespan.startOffset == startOffset) {
                        FoscInspection(parent).spanners.do { |spanner|
                            index = spanner.prIndexOf(parent);
                            spanner.prFracture(index, direction: 'left');
                        };
                    };
                    if (parent === component) { break.value };
                };
            };
        };
        // crawl back up through duration-crossing containers and split each
        previous = highestLevelComponentRightOfSplit;
        durationCrossingContainers.reverseDo { |container|
            // assert isinstance(container, abjad.Container)
            index = container.indexOf(previous);
            # left, right = container.prSplitAtIndex(index, fractureSpanners);
            previous = right;
        };
        // If logical tie here is convenience, then fusing is good.
        // If logical tie here is user-given, then fusing is less good.
        // Maybe later model difference between user logical ties and not.
        leftLogicalTie = FoscInspection(leafLeftOfSplit).logicalTie;
        rightLogicalTie = FoscInspection(leafRightOfSplit).logicalTie;
        leftLogicalTie.prFuseLeavesByImmediateParent;
        rightLogicalTie.prFuseLeavesByImmediateParent;
        // reapply tie here if crawl above killed tie applied to leaves
        if (didSplitLeaf) {
            if (tieSplitNotes && { leafLeftOfSplit.isKindOf(FoscNote) }) {
                if (FoscInspection(leafLeftOfSplit).parentage.root ===
                    FoscInspection(leafRightOfSplit).parentage.root) {
                    leavesAroundSplit = [leafLeftOfSplit, leafRightOfSplit];
                    selection = FoscSelection(leavesAroundSplit);
                    selection.prAttachTieToLeaves;
                };
            };
        };
        // return list-wrapped halves of container
        ^[[left], [right]];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prRemovePowersOfTwo
    -------------------------------------------------------------------------------------------------------- */
    *prRemovePowersOfTwo { |n|
        if (n.isKindOf(Integer).not || { n <= 0 }) {
            ^throw("%:%: n must be a positive Integer: %.".format(this.species, thisMethod.name, n));
        };

        while { n % 2 == 0 } { n = n.div(2) };
        
        ^n;
    }
}
