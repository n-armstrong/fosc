/* ------------------------------------------------------------------------------------------------------------
• FoscParentage

Parentage of a component.

!!!TODO: incomplete

a = FoscNote(60, 1/4);
b = FoscVoice([a]);
p = FoscParentage(a);
p.components;
------------------------------------------------------------------------------------------------------------ */
FoscParentage : FoscSequence {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <component;
	*new { |component, graceNotes=false| 
        //assert([FoscComponent, Nil].any { |type| component.isKindOf(type) });
        ^super.new.init(component, graceNotes);
	}
	init { |argComponent, graceNotes|
		var parent, type;
        
        component = argComponent;
        items = [];
        
        if (component.notNil) {
            parent = component;
            type = [FoscAfterGraceContainer, FoscGraceContainer];
            
            while { parent.notNil } {
                items = items.add(parent);
                
                if (graceNotes && { type.any { |type| parent.isKindOf(type) } }) {
                    parent = parent.mainLeaf;
                } {
                    parent = parent.parent;
                };
            };
        };
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • component
    
    The component from which the selection was derived.
    
    Returns component.
    
    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    p = FoscParentage(a);
    p.component == a;   // true
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • components

    Gets components.

    Returns array.

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    p = FoscParentage(a);
    p.components;
    -------------------------------------------------------------------------------------------------------- */
    components {
        ^this.items;
    }
    /* --------------------------------------------------------------------------------------------------------
    • depth

    Length of proper parentage of component.
    
    Returns integer.

    a = FoscNote(60, 1);
    b = FoscContainer([FoscRhythm([3, 4], [a, 2, 1])]);
    p = FoscParentage(a);
    p.depth;
    -------------------------------------------------------------------------------------------------------- */
    // DEPRECATED
    // depth {
    //     ^items[1..].size;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • isOrphan
    
    Is true when component has no parent. Otherwise false.
    
    Returns true or false.

    a = FoscNote(60, 1);
    b = FoscVoice([a]);
    p = FoscParentage(a);
    p.isOrphan;         // false

    a = FoscNote(60, 1);
    p = FoscParentage(a);
    p.isOrphan;         // true
    -------------------------------------------------------------------------------------------------------- */
    isOrphan {
       ^this.parent.isNil; 
    }
    /* --------------------------------------------------------------------------------------------------------
    • parent

    Gets parent. Returns nil when component has no parent.

    Returns component or nil.
    
    a = FoscNote(60, 1);
    b = FoscVoice([a]);
    p = FoscParentage(a);
    p.parent === b;     // true

    a = FoscNote(60, 1);
    p = FoscParentage(a);
    p.parent;           // nil
    -------------------------------------------------------------------------------------------------------- */
    parent {
        if (items.size > 1) { ^items[1] } { ^nil };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prolation

    Gets prolation.
    
    Returns multiplier.

    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    a[0].prGetParentage.prolation.str;
    -------------------------------------------------------------------------------------------------------- */
    prolation {
        var prolations, product;
        
        prolations = [FoscMultiplier(1)] ++ this.prProlations;
        product = prolations.reduce('*');
        
        ^product;
    }
    /* --------------------------------------------------------------------------------------------------------
    • root

    Root is last component in parentage.

    Returns component.

    a = FoscNote(60, 1);
    b = FoscVoice([a]);
    p = FoscParentage(a);
    p.root == b;
    -------------------------------------------------------------------------------------------------------- */
    root {
        ^items[items.lastIndex];
    }
    /* --------------------------------------------------------------------------------------------------------
    • scoreIndex

    Gets score index.

    Returns array of zero or more nonnegative integers.
    
    a = FoscNote(60, 1);
    b = FoscScore([FoscStaff([FoscVoice([a])])]);
    p = FoscParentage(a);
    p.scoreIndex;
    // a.prGetParentage.scoreIndex
    -------------------------------------------------------------------------------------------------------- */
    // DEPRECATED
    scoreIndex {
        var result, current, index;
        
        result = [];
        
        current = this[0];

        this[1..].do { |parent|
            index = parent.indexOf(current);
            result = result.insert(0, index);
            current = parent;
        };
        
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • firstInstanceOf (abjad: get_first)

    Gets first instance of type in parentage.

    Returns component or none.
    
    a = FoscNote(60, 1);
    b = FoscContainer([c = FoscRhythm([3, 4], [a, 2, 1])]);
    p = FoscParentage(a);

    p.firstInstanceOf(FoscNote) === a;          // true
    p.firstInstanceOf(FoscRhythm) === c;        // true
    p.firstInstanceOf(FoscContainer) === b;     // true
    p.firstInstanceOf(FoscRest);                // nil
    -------------------------------------------------------------------------------------------------------- */
    firstInstanceOf { |type|
        if (type.isNil) { type = FoscComponent };
        if (type.isSequenceableCollection.not) { type = [type] };
        
        items.do { |component|
            if (type.any { |type| component.isKindOf(type) }) { ^component };
        };
        
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • logicalVoice

    Gets logical voice of items.

    a = FoscLeafMaker().(#[60,62,64,65], [1/4]);
    b = FoscVoice(a);
    c = FoscStaff([b], name: 'bar');
    d = FoscScore([c], name: 'foo');
    p = FoscParentage(b[0]);
    l = p.logicalVoice;
    -------------------------------------------------------------------------------------------------------- */
    logicalVoice {
        var keys, logicalVoice;
        
        keys = #['score', 'staff_group', 'staff', 'voice'];
        logicalVoice = ();
        keys.do { |key| logicalVoice[key] = "" };
        
        items.do { |component|
            case
            { component.isKindOf(FoscVoice) } {
                if (logicalVoice['voice'].isEmpty) {
                    logicalVoice['voice'] = FoscParentage.prIDString(component);
                };
            }
            { component.isKindOf(FoscStaff) } {
                if (logicalVoice['staff'].isEmpty) {
                    logicalVoice['staff'] = FoscParentage.prIDString(component);
                    // explicit staff demands a nested voice
                    // if no explicit voice has been found
                    // create implicit voice here with random integer
                    if (logicalVoice['voice'].isEmpty) {
                        logicalVoice['voice'] = component.identityHash;
                    };
                };
            }
            { component.isKindOf(FoscStaffGroup) } {
                if (logicalVoice['staff_group'].isEmpty) {
                    logicalVoice['staff_group'] = FoscParentage.prIDString(component);
                };
            }
            { component.isKindOf(FoscScore) } {
                if (logicalVoice['score'].isEmpty) {
                    logicalVoice['score'] = FoscParentage.prIDString(component);
                };
            };
        };
        
        ^logicalVoice;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prIDString
    
    a = FoscNote(60, 1);
    b = FoscVoice([a]);
    p = FoscParentage(b);
    FoscParentage.prIDString(p.component);

    a = FoscNote(60, 1);
    a = FoscVoice(a).name_('voice_1');
    p = FoscParentage(a);
    FoscParentage.prIDString(p.component);
    -------------------------------------------------------------------------------------------------------- */
    *prIDString { |component|
        var lhs, rhs;
        
        lhs = component.species.name;
        
        if (component.respondsTo('name') && { component.name.notNil }) {
            rhs = component.name;
        } {
            rhs = component.identityHash;
        };
        
        ^"%-%".format(lhs, rhs);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prProlations

    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    p = FoscParentage(a[0]);
    p.prProlations.do { |e| [e, e.str].postln };
    -------------------------------------------------------------------------------------------------------- */
    prProlations {
        var prolations, default, prolation;
        
        prolations = [];
        default = FoscMultiplier(1);
        
        this.do { |parent|
            if (parent.respondsTo('impliedProlation')) {
                prolation = parent.impliedProlation;
            } {
                prolation = default;
            };
            
            prolations = prolations.add(prolation);
        };
        
        ^prolations;
    }
}
