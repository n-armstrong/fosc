/* ------------------------------------------------------------------------------------------------------------
• FoscLineage

Lineage of a component.

a = FoscStaff([FoscVoice([FoscNote(60, 1/4)])]);
b = FoscStaff([FoscVoice([FoscNote(59, 1/4)])]);
c = FoscScore([a, b]);
FoscLineage(c).components.printAll;
------------------------------------------------------------------------------------------------------------ */
FoscLineage : FoscSelection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <component, <components;
    *new { |component|
        if ([FoscComponent, nil].any { |type| component.isKindOf(type) }.not) {
            ^throw("FoscLineage:new: component must be a FoscComponent or nil: %.".format(component));
        };
        ^super.new.initFoscLineage(component);
    }
    initFoscLineage { |argComponent|
        component = argComponent;
        components = [];
        if (component.notNil) {
            components = components.addAll(component.prGetParentage[1..].reverse);
            components = components.add(component);
            components = components.addAll(FoscInspection(component).descendants[1..]);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • component
    
    The component from which the selection was derived.

    a = FoscStaff([FoscVoice([FoscNote(60, 1/4)])]);
    b = FoscStaff([FoscVoice([FoscNote(59, 1/4)])]);
    c = FoscScore([a, b]);
    d = FoscLineage(c);
    d.component;
    -------------------------------------------------------------------------------------------------------- */
}
