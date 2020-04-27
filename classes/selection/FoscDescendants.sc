/* ------------------------------------------------------------------------------------------------------------
• FoscDescendants

Descendants of a component.

Descendants is treated as the selection of the component's improper descendants.


a = FoscVoice([FoscNote(60, [1, 4])]);
b = FoscDescendants(a);
b.do { |each| each.postln };
b.last;
b.component;
------------------------------------------------------------------------------------------------------------ */
FoscDescendants : FoscSequence {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <component, <components;
    *new { |component, crossOffset, includeSelf=true|
        var components, result, appendEach;
        if (component.isNil) {
            components = [];
        } {
            components = component.selectComponents;
        };
        result = [];
        if (crossOffset.isNil) {
            result = components;
        } {
            components.do { |component|
                appendEach = true;
                if (
                    (
                        component.prGetTimespan.startOffset < crossOffset
                        && { crossOffset < component.prGetTimespan.stopOffset }
                    ).not
                ) {
                    appendEach = false; 
                };
                if (appendEach) { result = result.add(component) };
            };
        };
        ^super.new(result).initFoscDescendants(component);
    }
    initFoscDescendants { |argComponent|
        component = argComponent;
        components = items;
    }
}
