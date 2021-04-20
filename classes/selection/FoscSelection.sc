/* ------------------------------------------------------------------------------------------------------------
• FoscSelection

Selection of items (components / or other selections).

m = [FoscNote(60, 1/4), FoscNote(62, 1/4)];
a = FoscSelection(m);
a.items;

FoscSelection.dumpInterface
------------------------------------------------------------------------------------------------------------ */
FoscSelection : FoscSequence {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <player;
    *new { |items|
        if (items.isKindOf(FoscComponent)) { items = [items] };
        
        this.prCheck(items);
        
        ^super.new(items);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • items

    a = FoscSelection([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.items;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prCheck

    m = [FoscNote(60, 1/4), FoscNote(62, 1/4), 12];
    a = FoscSelection(m);
    -------------------------------------------------------------------------------------------------------- */
    *prCheck { |items|
        var prototype;
        
        prototype = [FoscComponent, FoscSelection];
        
        items.do { |item|
            if (prototype.any { |type| item.isKindOf(type) }.not) {
                throw("%:new: 'items' must contain components and/or selections: %"
                    .format(this.species, item));
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when selection and expr are of the same type and when items of selection equals items of expr. Otherwise false.

    Returns true or false.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a.items.copy);
    c = FoscSelection([FoscNote(64, 1/4)]);

    a == b;
    a == c;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • !=

    Is true when selection does not equal expr. Otherwise false.

    Returns true or false.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a.items.copy);
    c = FoscSelection([FoscNote(64, 1/4)]);

    a != b;
    a != c;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • ++

    Concatenates 'object' to selection.

    Returns new selection.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection([FoscNote(64, 1/4)]);
    c = a ++ b;
    c.items.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[items];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • any

    a = FoscSelection([FoscNote(60, 1/4), FoscChord(#[60,66,67], 1/4)]);
    a.any { |item| item.isKindOf(FoscNote) };
    a.any { |item| item.isKindOf(FoscChord) };
    a.any { |item| item.isKindOf(FoscRest) };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • areContiguousLogicalVoice

    Is true when items in selection are contiguous components in the same logical voice.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0..].areContiguousLogicalVoice;

    b = FoscSelection([a.leafAt(1), a.leafAt(3)]);
    b[0..].areContiguousLogicalVoice;
    -------------------------------------------------------------------------------------------------------- */
    areContiguousLogicalVoice { |prototype|
        var allowableTypes, first, firstParentage, firstLogicalVoice, firstRoot, previous, currentParentage;
        var currentLogicalVoice;
       
        allowableTypes = [SequenceableCollection, FoscSelection];
        if (allowableTypes.any { |type| this.isKindOf(type) }.not) { ^false };
        
        prototype = prototype ?? [FoscComponent];
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        if (this.size == 0) { ^true };

        if (
            items.every { |each|
                prototype.any { |type| each.isKindOf(type) }
                && { each.prGetParentage.isOrphan };
            }
        ) {
            ^true;
        };

        first = this[0];
        if (prototype.any { |type| first.isKindOf(type) }.not)  { ^false };
    
        firstParentage = first.prGetParentage(graceNotes: true);
        firstLogicalVoice = firstParentage.logicalVoice;
        firstRoot = firstParentage.root;
        previous = first;

        this[1..].do { |current|
            currentParentage = current.prGetParentage(graceNotes: true);
            currentLogicalVoice = currentParentage.logicalVoice;
            if (prototype.any { |type| current.isKindOf(type) }.not) { ^false };
            if (currentLogicalVoice != firstLogicalVoice) { ^false };
            
            if (currentParentage.root == firstRoot) {
                if (previous.prImmediatelyPrecedes(current).not) { ^false };
            };
            
            previous = current;
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • areContiguousSameParent

    Is true when items in selection are all contiguous components in the same parent.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0..].areContiguousSameParent;

    b = FoscSelection([a.leafAt(1), a.leafAt(3)]);
    b[0..].areContiguousSameParent;
    -------------------------------------------------------------------------------------------------------- */
    areContiguousSameParent { |prototype|
        var sameParent=true, strictlyContiguous=true, first, firstParent, previous;

        prototype = prototype ?? [FoscComponent];
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        if (this.size == 0) { ^true };

        if (
            items.every { |each|
                prototype.any { |type| each.isKindOf(type) }
                && { each.prGetParentage.isOrphan };
            }
        ) {
            ^true;
        };

        first = this[0];
        if (this.any { |type| first.isKindOf(type) }.not) { ^false };
        firstParent = first.parent;
        previous = first;

        this[1..].do { |current|
            if (prototype.any { |type| current.isKindOf(type) }.not) { ^false };
            if (current.parent != firstParent) { sameParent = false };
            if (previous.prImmediatelyPrecedes(current).not) { strictlyContiguous = false };
            
            if (
                current.prGetParentage.isOrphan.not
                && { sameParent.not || strictlyContiguous.not }
            ) {
                ^false;
            };
            
            previous = current;
        };
        
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • areLeaves

    Is true when items in selection are all leaves.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0..].areLeaves;
    -------------------------------------------------------------------------------------------------------- */
    areLeaves {
        ^items.every { |each| each.isKindOf(FoscLeaf) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • areLogicalVoice

    Is true when items in selection are all components in the same logical voice.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0..].areLogicalVoice;

    b = FoscSelection([a.leafAt(1), a.leafAt(3)]);
    b[0..].areLogicalVoice;
    -------------------------------------------------------------------------------------------------------- */
    areLogicalVoice { |prototype|
        var sameLogicalVoice=true, first, parentage, firstLogicalVoice;
        
        prototype = prototype ?? [FoscComponent];
        
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };   
        if (this.size == 0) { ^true };

        if (
            items.every { |each|
                prototype.any { |type| each.isKindOf(type) }
                && { each.prGetParentage.isOrphan };
            }
        ) {
            ^true;
        };

        first = this[0];
        if (prototype.any { |type| first.isKindOf(type) }.not)  { ^false };
        parentage = first.prGetParentage(graceNotes: true);
        firstLogicalVoice = parentage.logicalVoice;

        this[1..].do { |current|
            parentage = current.prGetParentage(graceNotes: true);
            
            if (parentage.logicalVoice != firstLogicalVoice) {
                sameLogicalVoice = false;
            };
            
            if (parentage.isOrphan.not && sameLogicalVoice.not) {
                ^false;
            };
        };
        
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at

    Gets item identified by index.

    Returns component from selection.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a[1].str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • atAll

    Gets items identified by indices.

    Returns components from selection.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    a.atAll(#[0,2]).do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • collect
    
    Collects components by predicate function.

    Returns a new selection.
    
    a = FoscLeafMaker().(#[60, 61, 62, 63, 64], [1/8]);
    b = a.collect { |each| each.writtenPitch_(each.writtenPitch + 12) };
    b.items.do { |each| each.writtenPitch.pitchNumber.postln };
    -------------------------------------------------------------------------------------------------------- */
    collect { |function|
        var newItems;
        
        newItems = this.items.collect(function);
        
        ^this.species.new(newItems);
    }
    /* --------------------------------------------------------------------------------------------------------
    • components

    Select components.

    Return new selection.
    
    • all components

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).components;
    b.do { |each| each.str.postln };

    • notes
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).components(prototype: FoscNote);
    b.do { |each| each.str.postln };

    • notes and rests
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).components(prototype: [FoscNote, FoscRest]);
    b.do { |each| each.str.postln };

    • notes and rests in reverse order
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).components(prototype: [FoscNote, FoscRest], reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    components { |prototype, exclude, graceNotes=false, reverse=false|
        var components;
        
        components = FoscIteration(this).components(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
        
        components = all(components);
        
        ^FoscSelection(components);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copySeries

    Gets slice of items identified by indices.

    Returns a new selection.
    
    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    b = a[1..];
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    copySeries { |size, start, step|
        var newItems;
        
        newItems = super.copySeries(size, start, step);
        
        ^FoscSelection(newItems);
    }
    /* --------------------------------------------------------------------------------------------------------
    • do

    Iterates over items in selection.

    a = FoscLeafMaker().(#[60,60,62,64,65,65], [1/4,1/24,1/12,1/8,1/4,1/4]);
    a.show;

    • does not recurse into containers:

    FoscSelection(a).do { |each| each.postln };

    • recurses into containers:

    FoscSelection(a).do({ |each| each.postln }, recurse: true);
    -------------------------------------------------------------------------------------------------------- */
    do { |function, recurse=false|
        if (recurse) {
            FoscIteration(this).components.do(function);
        } {
            super.do(function);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • doAdjacentPairs

    Iterates over adjacent pairs of items in selection.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    a.doAdjacentPairs { |a, b| [a.str, b.str].postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • every

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.every { |item| item.isKindOf(FoscNote) };

    a = FoscSelection([FoscNote(60, 1/4), FoscChord(#[60,66,67], 1/4)]);
    a.every { |item| item.isKindOf(FoscNote) };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • first

    Gets first items in selection.

    Returns component.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    a.first.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • flat

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection([FoscRest(1/4), FoscChord(#[60,66,67], 1/4)]);
    c = FoscSelection([a, b]);
    c.items;

    d = c.flat;
    d.items;
    -------------------------------------------------------------------------------------------------------- */
    flat {
        var prototype, result, recurse;
        
        result = [];
        prototype = [FoscSelection, SequenceableCollection];
        
        recurse = { |val|
            val.do { |each|
                if (prototype.any { |type| each.isKindOf(type) }) {
                    recurse.(each);
                } {
                    result = result.add(each);
                };
            };
        };
        
        recurse.(this);
        
        ^this.species.new(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • groupBy (synonymous with 'separate')
    
    Groups components by predicate function.

    Returns a selection of selections.
    
    a = FoscLeafMaker().(#[nil, 60, 61, nil, 62, 63, 64], [1/8]);
    b = a.leaves.groupBy { |a, b| a.isPitched != b.isPitched };
    b.do { |each| each.items.postln };
    -------------------------------------------------------------------------------------------------------- */
    groupBy { |function|
        ^this.separate(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • groupByContiguity

    Group items in selection by contiguity.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    a.show;

    • Group pitched leaves by contiguity.

    m = a.selectLeaves(pitched: true);
    m = m.groupByContiguity;
    m.do { |each| each.items.collect { |item| item.str }.postln };

    a.consistsCommands.add('Horizontal_bracket_engraver');
    t = #[['bracket-flare', [0,0]], ['direction', 'up'], ['staff-padding', 3]];
    m.do { |each| each.horizontalBracket(tweaks: t) };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    groupByContiguity {
        var result, selection, timespanA, timespanB;
        
        result = [];
        selection = [this[0]];
        
        this[1..].do { |item|
            timespanA = selection.last.prGetTimespan;
            timespanB = item.prGetTimespan;
            if (timespanA.stopOffset == timespanB.startOffset) {
                selection = selection.add(item);
            } {
                result = result.add(this.species.new(selection));
                selection = [item];
            };
        };
        
        if (selection.notEmpty) { result = result.add(this.species.new(selection)) };
        
        ^this.species.new(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • groupByPitch

    Group items in selection by pitch.

    a = FoscStaff(FoscLeafMaker().(#[60,[60,64,67],62,62,62,nil,[65,69],[65,69]], [1/8]));
    m = a.selectLeaves;
    m[2..3].tie;
    m = a.selectLogicalTies.groupByPitch;
    m.items.do { |each| each.items.collect { |item| item.str }.postln };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    groupByPitch {
        ^this.leaves(pitched: true).separate { |a, b|
            case
            { a.respondsTo('writtenPitch') && b.respondsTo('writtenPitch') } {
                a.writtenPitch != b.writtenPitch;
            }
            { a.respondsTo('writtenPitches') && b.respondsTo('writtenPitches') } {
                a.writtenPitches != b.writtenPitches;
            }
            { true };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • groupByPitched

    Group items in selection by pitched and non-pitched leaves.

    a = FoscStaff(FoscLeafMaker().(#[60,[60,64,67],62,62,62,nil,[65,69],[65,69]], [1/8]));
    m = a.selectLeaves;
    m[2..3].tie;
    m = a.selectLogicalTies.groupByPitched;
    m.items.do { |each| each.items.collect { |item| item.str }.postln };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    // groupByPitched {
    //     ^this.leaves.separate { |a, b| a.isPitched != b.isPitched };
    // }
    /* --------------------------------------------------------------------------------------------------------
    • hash
    • TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • illustrate 

    Attempts to illustrate selection. The illustration will usually work for simple selections that represent a contiguous snippet of a single voice of items.

    Returns LilyPond file.


    • regular staff

    a = FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]);
    a.show;

    
    • RhythmicStaff if all pitches = middle-c

    a = FoscLeafMaker().(60 ! 8, [1/8]);
    a.show;

    FoscComponent
    -------------------------------------------------------------------------------------------------------- */
    illustrate {
        var items, staff, foundDifferentPitch, score, lilypondFile;
        
        items = this.deepCopy.flat;
        staff = FoscStaff(items);
        foundDifferentPitch = false;
        
        FoscIteration(staff).pitches.do { |pitch|
            block { |break|
                if (pitch != FoscPitch(60)) {
                    foundDifferentPitch = true;
                    break.value;
                };
            };
        };
        
        if (foundDifferentPitch.not) { staff.lilypondType_('RhythmicStaff') };
        
        score = FoscScore([staff]);
        
        // lilypondFile = FoscLilypondFile(score);
        // lilypondFile.headerBlock.tagline = false;
        // ^lilypondFile;
        ^score.illustrate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • includes (abjad: __contains__)

    Is true when expr is in selection. Otherwise false.

    Returns true or false.

    x = FoscNote(60, 1/4);
    y = FoscNote(62, 1/4);
    a = FoscSelection([x]);
    a.includes(x);
    a.includes(y);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • indexOf

    x = FoscNote(60, 1/4);
    y = FoscNote(62, 1/4);
    a = FoscSelection([x, y]);
    a.indexOf(x);
    a.indexOf(y);
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • insert

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.do { |each| each.str.postln };

    a.insert(1, FoscNote(72, 1/4));
    a.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • isEmpty

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.isEmpty;

    a = FoscSelection([]);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • last

    Gets last item in selection.

    Returns component.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    a.last.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lastIndex

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.lastIndex;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leafAt

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;

    a = FoscSelection([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0, pitched: true).str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leaves

    Selects leaves.

    Returns new selection.

    • all leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).leaves;
    b.do { |each| each.str.postln };

    • pitched leaves
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).leaves(pitched: true);
    b.do { |each| each.str.postln };

    • non-pitched leaves
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).leaves(pitched: false);
    b.do { |each| each.str.postln };

    • leaves in reverse order
    
    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = FoscSelection(a).leaves(reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    leaves { |prototype, exclude, graceNotes=false, pitched, reverse=false|
        prototype = prototype ? FoscLeaf;
        
        case 
        { pitched == true } {
            prototype = [FoscChord, FoscNote];
        }
        { pitched == false } {
            prototype = [FoscMultimeasureRest, FoscRest, FoscSkip];
        };
        
        ^this.components(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • logicalTies

    Selects logical ties.

    Returns new selection.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    a.show;


    • select all logicalTies

    b = FoscSelection(a).logicalTies;
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select pitched logicalTies
    
    b = FoscSelection(a).logicalTies(pitched: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select non-pitched logicalTies
    
    b = FoscSelection(a).logicalTies(pitched: false);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select nontrivial logicalTies
    
    b = FoscSelection(a).logicalTies(nontrivial: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select trivial logicalTies
    
    b = FoscSelection(a).logicalTies(nontrivial: false);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select logicalTies in reverse order
    
    b = FoscSelection(a).logicalTies(reverse: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };
    -------------------------------------------------------------------------------------------------------- */
    logicalTies { |exclude, graceNotes=false, nontrivial, pitched, reverse=false|
        var components;
        
        components = FoscIteration(this).logicalTies(
            exclude: exclude,
            graceNotes: graceNotes,
            nontrivial: nontrivial,
            pitched: pitched,
            reverse: reverse
        );
        
        components = all(components);
        
        ^FoscSelection(components);
    }
    /* --------------------------------------------------------------------------------------------------------
    • notEmpty

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.notEmpty;

    a = FoscSelection([]);
    a.notEmpty;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • reverseDo

    Iterates over all components in selection in reverse.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.reverseDo { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • partitionByDurations

    Partitions selection by durations.

    Parts must equal durations exactly when 'fill' is 'exact'.

    Parts must be less than or equal to durations when 'fill' is 'less'.

    Parts must be greater or equal to durations when 'fill' is 'more'.

    Reads durations cyclically when 'isCylic' is true.

    Reads component durations in seconds when 'inSeconds' is true.

    Returns remaining components at end in final part when 'overhang' is true.

    Returns array of selections.


    • Example 1

    Cyclically partitions leaves into parts equal to exactly 3/8, with overhang returned at end.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    b = a[0..].partitionByDurations([3/8], isCyclic: true, overhang: true);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    a.show;


    • Example 2

    Partitions leaves into one part equal to exactly 3/8, truncating overhang.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    b = a[0..].partitionByDurations([3/8], isCyclic: false, overhang: false);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    a.show;


    • Example 3

    Cyclically partitions leaves into parts equal to (or just less than) 3/16 and 1/16, with overhang returned at end.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    b = a[0..].partitionByDurations([3/16,1/16], isCyclic: true, fill: 'more', overhang: true);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    a.show;
    
    
    • Example 4

     Cyclically partitions leaves into parts equal to (or just less than) 3/16, truncating overhang.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    b = a[0..].partitionByDurations([3/16], isCyclic: true, fill: 'less', overhang: false);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    a.show;
    

    • Example 5

    Partitions leaves into a single part equal to (or just less than) 3/16, truncating overhang.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    b = a[0..].partitionByDurations([3/16], isCyclic: false, fill: 'less', overhang: false);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    a.show;


    • Example 6

    Cyclically partitions exactly 1.5 seconds at a time.


    • Example 7

    Cyclically partitions exactly 1.5 seconds at a time with overhang returned at end.


    • Example 8

    Partitions exactly 1.5 seconds one time.


    • Example 9

    Cyclically partitions 0.75 seconds with part durations allowed to be just less than 0.75 seconds.


    • Example 10

    Partitions 0.75 seconds just once with part duration allowed to be just less than 0.75 seconds.

    -------------------------------------------------------------------------------------------------------- */
    partitionByDurations { |durations, isCyclic=false, fill='exact', inSeconds=false, overhang=false|
        var result, part, currentDurationIndex, targetDuration, cumulativeDuration, componentsCopy;
        var candidateDuration, component, componentDuration;

        durations = durations.collect { |each| FoscDuration(each) };
        if (isCyclic) { durations = FoscCyclicArray(durations) };
        result = [];
        part = [];
        currentDurationIndex = 0; 
        targetDuration = durations[currentDurationIndex];
        cumulativeDuration = FoscDuration(0);
        componentsCopy = items.copy;

        block { |break|
            while { true } {
                if (componentsCopy.isEmpty) { break.value };
                component = componentsCopy.removeAt(0);
                componentDuration = component.prGetDuration;
                
                if (inSeconds) {
                    componentDuration = component.prGetDuration(inSeconds: true);
                };
                
                candidateDuration = cumulativeDuration + componentDuration;

                case
                { candidateDuration < targetDuration } {
                    part = part.add(component);
                    cumulativeDuration = candidateDuration;
                }
                { candidateDuration == targetDuration } {
                    part = part.add(component);
                    result = result.add(part);
                    part = [];
                    cumulativeDuration = FoscDuration(0);
                    currentDurationIndex = currentDurationIndex + 1;
                    targetDuration = durations[currentDurationIndex];
                    if (targetDuration.isNil) { break.value };
                }
                { targetDuration < candidateDuration } {
                    case { fill == 'exact' } {
                        throw("%:%: must partition exactly.".format(this.species, thisMethod.name));
                    }
                    { fill == 'less' } {
                        result = result.add(part);
                        part = [component];
                        cumulativeDuration = part.collect { |each| each.prGetDuration(inSeconds) }.sum; 
                        currentDurationIndex = currentDurationIndex + 1;
                        targetDuration = durations[currentDurationIndex];
                        if (targetDuration.isNil) { break.value };
                        
                        if (targetDuration < cumulativeDuration) {
                            throw(
                                "%:%: target duration % is less than cumulative duration %."
                                    .format(this.species, thisMethod.name, targetDuration, cumulativeDuration);
                            );
                        };
                    }
                    { fill == 'more' } {
                        part = part.add(component);
                        result = result.add(part);
                        part = [];
                        cumulativeDuration = FoscDuration(0);
                        currentDurationIndex = currentDurationIndex + 1;
                        targetDuration = durations[currentDurationIndex];
                        if (targetDuration.isNil) { break.value };
                    };
                }
                
            };
        };

        if (part.notEmpty && overhang) { result = result.add(part) };
        if (componentsCopy.notEmpty && overhang) { result = result.add(componentsCopy) };
        result = result.collect { |each| FoscSelection(each) };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionByRatio

    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8,1/8,1/8,1/8]));
    b = a[0..].partitionByRatio(#[5, 11, 4]);
    b.do { |sel| sel.items.collect { |each| each.str }.postln };
    b.do { |sel| if (sel.size > 1) { sel.slur } };
    b.items.postln;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    partitionByRatio { |ratio|
        var sizes, parts, selections;
        
        //• TODO: ratio = FoscRatio(ratio);
        sizes = this.size.partitionByRatio(ratio);
        parts = this.partitionBySizes(sizes);
        selections = parts.items.collect { |each| this.species.new(each) };
        
        ^this.species.new(selections);
    }
    /* --------------------------------------------------------------------------------------------------------
    • partitionBySizes

    • TODO: 'overhang' argument rather than 'isCyclic'

    a = FoscLeafMaker().((60..72), [1/8]);
    a = a.partitionBySizes([2,3,5,2,1]);
    a.do { |each| if (each.size > 1) { each.attach(FoscSlur('below')) } };
    a.show;

    a = FoscLeafMaker().((60..72), [1/8]);
    a = a.partitionBySizes([2,3], isCyclic: true);
    a.do { |each| if (each.size > 1) { each.attach(FoscSlur('below')) } };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    partitionBySizes { |sizes, isCyclic=false|
        var selections;
        
        if (isCyclic) {
            sizes = sizes.repeatToAbsSum(this.size);
        } {
            sizes = sizes.extendToAbsSum(this.size);
        };
        
        selections = items.clumps(sizes).collect { |each| FoscSelection(each) };
        
        ^this.species.new(selections);
    }
    /* --------------------------------------------------------------------------------------------------------
    • put

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.do { |each| each.str.postln };

    a.put(1, FoscNote(72, 1/4));
    a.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • remove

    x = FoscNote(60, 1/4);
    y = FoscNote(62, 1/4);
    a = FoscSelection([x, y]);
    a.do { |each| each.str.postln };

    a.remove(x);
    a.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • removeAt

    x = FoscNote(60, 1/4);
    y = FoscNote(62, 1/4);
    a = FoscSelection([x, y]);
    a.do { |each| each.str.postln };

    a.removeAt(0);
    a.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • reverseDo

    Reverse-iterates over items in selection.


    a = FoscLeafMaker().(#[60,60,62,64,65,65], [1/4,1/24,1/12,1/8,1/4,1/4]);
    a.show;

    • does not recurse into containers:

    FoscSelection(a).reverseDo { |each| each.postln };

    • recurses into containers:

    FoscSelection(a).reverseDo({ |each| each.postln }, recurse: true);
    -------------------------------------------------------------------------------------------------------- */
    reverseDo { |function, recurse=false|
        if (recurse) {
            FoscIteration(this).components(reverse: true).do(function);
        } {
            super.reverseDo(function);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • runs

    Select runs.

    • Attach horizontal bracket to each run.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    a.consistsCommands.add('Horizontal_bracket_engraver');
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    m.runs.do { |each| each.horizontalBracket(tweaks: #[['direction', 'up']]) };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    // runs { |exclude|
    //     var result;
    //     result = this.leaves(exclude: exclude, pitched: true);
    //     result = result.groupByContiguity;
    //     ^result;
    // }
    runs { |exclude|
        var result;
        
        result = this.leaves(exclude: exclude);
        result = result.groupBy { |a, b| a.isPitched != b.isPitched };
        result = result.items.select { |each| each[0].isPitched };
        
        ^this.species.new(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • separate
    
    Groups components by predicate function.

    Returns a selection of selections.
    
    a = FoscLeafMaker().(#[nil, 60, 61, nil, 62, 63, 64], [1/8]);
    b = a.leaves.separate { |a, b| a.isPitched != b.isPitched };
    b.do { |each| each.items.postln };
    -------------------------------------------------------------------------------------------------------- */
    separate { |function|
        var newItems;
        
        function = function ? false;
        newItems = items.separate(function);
        newItems = newItems.collect { |each| FoscSelection(each) };
        
        ^this.species.new(newItems);
    }
    /* --------------------------------------------------------------------------------------------------------
    • size

    Gets number of components in selection.

    Returns integer.

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.size;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • timespan

    Gets timespan of contiguous selection.

    Returns timespan.
    
    a = FoscVoice({ FoscNote(60, [1, 8]) } ! 8);
    b = a.select.byLeaf[1..6];
    b.timespan;
    [b.timespan.startOffset.pair, b.timespan.stopOffset.pair].postln;
    FoscTimespan.superclass.dumpInterface;


    a = FoscMeasure([4, 4], [FoscNote(60, [1, 32]), FoscNote(62, [7, 8]), FoscNote(62, [1, 16]), FoscNote(64, [1, 32])]);
    a[1..2].attach(FoscTie());
    x = FoscStaff([FoscMeasure([2, 4], [FoscNote(60, [2, 4])]), a]);
    
    m = FoscMeterManager.iterateRewriteInputs(a).all;

    m.do { |logicalTie|
        t = logicalTie.timespan;
        [t.startOffset.pair, t.stopOffset.pair].postln
    };

    -- lost when measure is added to staff
    a[0].prGetTimespan.startOffset.pair;
    a[1].prGetTimespan.startOffset.pair;
    a[2].prGetTimespan.startOffset.pair;

    a[0].timespan.startOffset.pair;
    a[1].timespan.startOffset.pair;
    a[2].timespan.startOffset.pair;

    -------------------------------------------------------------------------------------------------------- */
    timespan { |inSeconds=false|
        var timespan, startOffset, stopOffset;
        
        if (inSeconds) { ^this.notYetImplemented(thisMethod) };
        
        timespan = this[0].prGetTimespan; //• TODO: BROKEN if this[0] is a selection
        startOffset = timespan.startOffset;
        stopOffset = timespan.stopOffset;
        
        this[1..].do { |each|
            timespan = each.prGetTimespan;
            if (timespan.startOffset < startOffset) { startOffset = timespan.startOffset };
            if (stopOffset < timespan.stopOffset) { stopOffset = timespan.stopOffset };
        };
        
        ^FoscTimespan(startOffset, stopOffset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • tuplets

    Select tuplets.

    • Attach horizontal bracket to each tuplet.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    a.consistsCommands.add('Horizontal_bracket_engraver');
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    FoscSelection(a).tuplets.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    tuplets { |exclude|
        ^this.components(prototype: FoscTuplet, exclude: exclude);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isPlaying

    a = FoscLeafMaker().(#[60,62,nil].wrapExtend(12), [3/8,1/8,5/16,1/16]);
    a.play;
    a.isPlaying;
    a.stop;
    a.isPlaying;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • pause

    a = FoscLeafMaker().(#[60,62,nil].wrapExtend(12), [3/8,1/8,5/16,1/16]);
    a.play;
    a.pause;
    a.resume;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • play

    a = FoscLeafMaker().(#[60,62,nil].wrapExtend(12), [3/8,1/8,5/16,1/16]);
    a.play;
    a.stop;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • resume

    a = FoscLeafMaker().(#[60,62,nil].wrapExtend(12), [3/8,1/8,5/16,1/16]);
    a.play;
    a.pause;
    a.resume;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stop

    a = FoscLeafMaker().(#[60,62,nil].wrapExtend(12), [3/8,1/8,5/16,1/16]);
    a.play;
    a.stop;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttachTieToLeaves

    a = FoscLeafMaker().([60], 1/4 ! 4);
    a.prAttachTieToLeaves;
    a.show;

    a = FoscLeafMaker().([60], 5/16 ! 4);
    a.prAttachTieToLeaves;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prAttachTieToLeaves { |repeatTies=false|
        var leaves;
        
        leaves = [];
        
        this.do { |leaf|
            assert(leaf.isKindOf(FoscLeaf));
            leaf.prGetLogicalTie.leaves.do { |each|
                if (leaves.includes(each).not) { leaves = leaves.add(leaf) };
            };
        };
        
        leaves = FoscSelection(leaves);
        
        leaves.do { |leaf|
            leaf.detach(FoscTie);
            leaf.detach(FoscRepeatTie);
        };
        
        leaves.tie(repeat: repeatTies);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCopy

    a = FoscLeafMaker().(#[60,62,64,65], 1/4);
    b = a.prCopy;
    -------------------------------------------------------------------------------------------------------- */
    prCopy {
        var newComponents, newComponent;
        
        assert(this.areContiguousLogicalVoice);
        newComponents = [];
        
        this.do { |component|
            if (component.isKindOf(FoscContainer)) {
                newComponent = component.prCopyWithChildren;
            } {
                newComponent = component.copy;
            };
            
            newComponents = newComponents.add(newComponent);
        };
        
        ^this.species.new(newComponents);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetachBeams

    Detaches beams from leaves, including all neighbouring leaves in enclosing tuplets.
    -------------------------------------------------------------------------------------------------------- */
    prDetachBeams {
        var leaves, tuplets, parentage;

        leaves = this.selectLeaves;
        tuplets = [];

        leaves.do { |leaf|
            parentage = leaf.prGetParentage;
            parentage.do { |item|
                if (item.isKindOf(FoscTuplet) && { tuplets.includes(item).not }) {
                    tuplets = tuplets.add(item);
                };
            };
        };

        tuplets.doLeaves { |leaf|
            if (leaves.includes(leaf).not) { leaves = leaves.add(leaf) };
        };

        leaves.do { |leaf|
            leaf.detach(FoscStartBeam);
            leaf.detach(FoscStopBeam);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetachTies

    Detaches ties from leaves.
    -------------------------------------------------------------------------------------------------------- */
    prDetachTies {
        this.selectLeaves.do { |leaf| leaf.detach(FoscTie) };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFuseLeaves

    • Example 1

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    a.selectLeaves.prFuseLeaves;
    a.show;


    • Example 2

    a = FoscStaff(FoscRhythmMaker().([1/4], #[[1,[4,[1,1,1,1,1]]],[1,1,1]]));
    mutate(a).rewritePitches((60..72));
    m = a.selectLeaves.partitionBySizes(#[1,6,2]);
    m.do { |part| part.prFuseLeaves };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prFuseLeaves { |silence=false|
        var leaves, groups, originallyTied, totalPreprolated, parent, index, result, lastLeaf;

        leaves = this.leaves;

        if (leaves.areContiguousLogicalVoice.not || { leaves.areLeaves.not }) {
            ^FoscMethodError(thisMethod, "all components must be contiguous leaves.").throw;
        };

        leaves = this;
        if (leaves.size <= 1) { ^leaves };
        leaves.prDetachBeams;

        groups = leaves.groupBy { |a, b| a.parent != b.parent };
        groups.doAdjacentPairs { |a, b| b.first.writtenPitch_(a.first.writtenPitch) };
        
        groups.do { |leaves, i|
            originallyTied = leaves.last.prHasIndicator(FoscTie);
            totalPreprolated = leaves.prGetPreprolatedDuration;

            leaves[1..].do { |leaf|
                parent = leaf.parent;
                if (parent.notNil) {
                    index = parent.indexOf(leaf);
                    parent.removeAt(index);
                };
            };

            result = leaves[0].prSetDuration(totalPreprolated);
            lastLeaf = result.last;

            if ((i == groups.lastIndex) && { originallyTied.not }) {
                lastLeaf.detach(FoscTie);
            } {
                lastLeaf.attach(FoscTie());
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFuseLeavesAndReplaceWithRests

    • TODO: selective detachment of indicators ?

    • Example 1

    a = FoscStaff(FoscRhythmMaker().([1/4], #[[1,[4,[1,1,1,1,1]]],[1,1,1]]));
    m = a.selectLeaves[1..6].prFuseLeavesAndReplaceWithRests;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prFuseLeavesAndReplaceWithRests {
        var leaves, groups, totalPreprolated, parent, index, leaf;

        leaves = this.leaves;

        if (leaves.areContiguousLogicalVoice.not || { leaves.areLeaves.not }) {
            ^FoscMethodError(thisMethod, "all components must be contiguous leaves.").throw;
        };

        leaves.prDetachBeams;
        leaves.prDetachTies;
        groups = leaves.groupBy { |a, b| a.parent != b.parent };
        
        groups.do { |leaves, i|
            totalPreprolated = leaves.prGetPreprolatedDuration;

            leaves.do { |leaf, i|
                parent = leaf.parent;

                if (parent.notNil) {
                    index = parent.indexOf(leaf);
                    if (i > 0) {
                        parent.removeAt(index);
                    } {
                        leaf = FoscRest(leaf).prSetDuration(totalPreprolated);
                        parent.prSetItem(index, leaf);
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetContentsDuration
    
    Gets summed duration of all leaves in selection.

    Returns duration.
    
    
    • Example 1

    m = FoscSelection({ FoscNote(60, 1/4) } ! 4);
    m.prGetContentsDuration.str;


    • Example 2

    a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]));
    a[0].attach(FoscMetronomeMark([1,4], 120));
    b = FoscScore([a]);
    b[0..].prGetContentsDuration(inSeconds: true).asFloat;
    -------------------------------------------------------------------------------------------------------- */
    prGetContentsDuration { |inSeconds=false|
        var durations, result;
        
        durations = [];
        
        this.leaves.do { |leaf|
            durations = durations.add(leaf.prGetDuration(inSeconds));
        };
        
        result = durations.sum;
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetParentAndStartStopIndices
    -------------------------------------------------------------------------------------------------------- */
    prGetParentAndStartStopIndices {
        var first, last, parent, firstIndex, lastIndex;
        
        if (this.areContiguousSameParent.not) {
            throw("%:%: components are not contiguous.".format(this.species, thisMethod.name));
        };
        
        if (this.size > 0) {
            # first, last = [items[0], items.last];
            parent = first.parent;
            
            if (parent.notNil) {
                firstIndex = parent.indexOf(first);
                lastIndex = parent.indexOf(last);
                ^[parent, firstIndex, lastIndex];
            };
        };
        
        ^[nil, nil, nil];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetPreprolatedDuration
    -------------------------------------------------------------------------------------------------------- */
    prGetPreprolatedDuration {
        ^items.collect { |component| component.prGetPreprolatedDuration }.sum;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetParents
    -------------------------------------------------------------------------------------------------------- */
    prSetParents { |newParent|
        items.do { |component| component.prSetParent(newParent) };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // • TODO: FOR DEPRECATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • byClass
    • TODO: DEPRECATE ! - use selection:components
    -------------------------------------------------------------------------------------------------------- */
    byClass { |prototype, condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byClass(prototype, condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFuse

    • TODO: DEPRECATE -- use 'prFuseLeaves' only
    
    a = FoscStaff([FoscLeafMaker().(#[60,60], 1/4), FoscTuplet(2/3, { FoscNote(60, 1/8) } ! 3)]);
    a.show;

    a.selectLeaves[0..2].prFuse;
    a.show;

    • TODO: tie components and then fuse by parent
    
    a = FoscStaff([FoscLeafMaker().(#[60,60], 1/4), FoscTuplet(2/3, { FoscNote(60, 1/8) } ! 3)]);
    a.selectLeaves[0..2].prAttachTieToLeaves;
    b = a.selectLogicalTies[0];
    b.prFuseLeavesByImmediateParent;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prFuse {
        if (this.areContiguousLogicalVoice.not) {
            throw("%:%: components must be contiguous and in same logical voice."
                .format(this.species, thisMethod.name));
        };
        case
        { items.every { |elem| elem.isKindOf(FoscLeaf) } } {
            ^this.prFuseLeaves;
        }
        { items.every { |elem| elem.isKindOf(FoscTuplet) } } {
            ^this.prFuseTuplets;
        }
        { items.every { |elem| elem.isKindOf(FoscMeasure) } } {
            ^this.prFuseMeasures;
        }
        {
            throw("%:%: can not fuse.".format(this.species, thisMethod.name));
        }; 
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLeaf

    • TODO: DEPRECATE ! - use selection:leaves
    -------------------------------------------------------------------------------------------------------- */
    byLeaf {
        var iterator;
        iterator = FoscIterationAgent(this).byLeaf;
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLogicalTie

    • TODO: DEPRECATE ! - use selection:logicalTies
    -------------------------------------------------------------------------------------------------------- */
    byLogicalTie { |condition=true, parentageMask|
        var iterator;
        iterator = FoscIterationAgent(this).byLogicalTie(condition, parentageMask);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedLeaf

    • TODO: DEPRECATE ! - use selection:leaves(pitched: true)
    -------------------------------------------------------------------------------------------------------- */
    byPitchedLeaf { |condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byPitchedLeaf(condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedLogicalTie

    • TODO: DEPRECATE ! - use selection:logicalTies(pitched: true)
    -------------------------------------------------------------------------------------------------------- */
    byPitchedLogicalTie { |condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byPitchedLogicalTie(condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedRun

    • TODO: DEPRECATE ! - use selection:runs(pitched: true)
    -------------------------------------------------------------------------------------------------------- */
    byPitchedRun { |condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byPitchedRun(condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byRun

    • TODO: DEPRECATE ! - use selection:runs
    -------------------------------------------------------------------------------------------------------- */
    byRun { |condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byRun(condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimeline

    • TODO: DEPRECATE ! - use selection:timeline
    -------------------------------------------------------------------------------------------------------- */
    byTimeline { |prototype, condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byTimeline(prototype, condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimelineAndLogicalTie

    • TODO: DEPRECATE !
    -------------------------------------------------------------------------------------------------------- */
    byTimelineAndLogicalTie { |prototype, condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byTimelineAndLogicalTie(prototype, condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimelineAndPitchedLogicalTie

    • TODO: DEPRECATE !
    -------------------------------------------------------------------------------------------------------- */
    byTimelineAndPitchedLogicalTie { |prototype, condition=true|
        var iterator;
        iterator = FoscIterationAgent(this).byTimelineAndPitchedLogicalTie(prototype, condition);
        ^FoscSelection(iterator.all);
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    • TODO: DEPRECATE ?
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^this.prGetContentsDuration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • flattenSelections

    • TODO: DEPRECATE ! - use selection:flat
    -------------------------------------------------------------------------------------------------------- */
    flattenSelections {
        var prototype, result, recurse;
        result = [];
        prototype = [FoscSelection, SequenceableCollection];
        recurse = { |val|
            val.do { |each|
                if (prototype.any { |type| each.isKindOf(type) }) {
                    recurse.(each);
                } {
                    result = result.add(each);
                };
            };
        };
        recurse.(this);
        items = result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • music

    • TODO: DEPRECATE !
    -------------------------------------------------------------------------------------------------------- */
    music {
        ^items;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateTopDown
    
    • TODO: DEPRECATE !
    -------------------------------------------------------------------------------------------------------- */
    prIterateTopDown {
        ^this.flat.items.prIterateTopDown;
    }
}
