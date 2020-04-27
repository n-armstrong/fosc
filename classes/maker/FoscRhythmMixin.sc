/* ------------------------------------------------------------------------------------------------------------
• FoscRhythmMixin

Shared interface for FoscRhythm and FoscRhythmLeaf.
------------------------------------------------------------------------------------------------------------ */
FoscRhythmMixin {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration
    -------------------------------------------------------------------------------------------------------- */
    duration { |node|
        ^(node.prolation * node.prGetPreprolatedDuration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • parentageRatios
    -------------------------------------------------------------------------------------------------------- */
    parentageRatios { |node|
        var result;
        result = [];
        while { node.parent.notNil } {
            result = result.add([node.prGetPreprolatedDuration, node.parent.prGetContentsDuration]);
            node = node.parent;
        };
        result = result.add(node.prGetPreprolatedDuration);
        result = result.reverse;
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prolation
    -------------------------------------------------------------------------------------------------------- */
    prolation { |node|
        ^node.prolations.reduce('*');
    }
    /* --------------------------------------------------------------------------------------------------------
    • prolations
    -------------------------------------------------------------------------------------------------------- */
    prolations { |node|
        var prolations, improperParentage;
        prolations = [FoscMultiplier(1)];
        improperParentage = node.improperParentage;
        improperParentage.doAdjacentPairs { |child, parent|
            prolations = prolations.add(
                FoscMultiplier(parent.prGetPreprolatedDuration, parent.prGetContentsDuration);
            );
        };
        ^prolations;
    }
    /* --------------------------------------------------------------------------------------------------------
    • startOffset
    -------------------------------------------------------------------------------------------------------- */
    startOffset { |node|
        node.prUpdateOffsetsOfEntireTree;
        ^node.offset;
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset
    -------------------------------------------------------------------------------------------------------- */
    stopOffset { |node|
        ^(node.startOffset + node.duration);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateOffsetsOfEntireTree
    -------------------------------------------------------------------------------------------------------- */
    prUpdateOffsetsOfEntireTree { |node|
        var recurse, offset, root, children, hasChildren;
        if (node.offsetsAreCurrent) { ^nil };
        recurse = { |container, currentOffset|
            container.instVarPut('offset', currentOffset);
            container.instVarPut('offsetsAreCurrent', true);
            container.items.do { |child|
                if (child.respondsTo('items') && { child.items.notEmpty }) {
                    currentOffset = recurse.(child, currentOffset);
                } {
                    child.instVarPut('offset', currentOffset);
                    child.instVarPut('offsetsAreCurrent', true);
                    currentOffset = currentOffset + child.duration;
                };
            };
            currentOffset;
        };
        root = node.root;
        offset = FoscOffset(0);
        try {
            children = node.items;
            hasChildren = children.notEmpty;
        } {
            hasChildren = false;
        };
        if (node === root && { hasChildren.not }) {
            node.instVarPut('offset', offset);
            node.instVarPut('offsetsAreCurrent', true);
        } {
            recurse.(root, offset);
        };
    }
}
