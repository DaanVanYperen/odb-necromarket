package net.mostlyoriginal.game.system.action;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import net.mostlyoriginal.api.component.graphics.RendererSingleton;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.action.ActionDrop;
import net.mostlyoriginal.game.component.inventory.Inventory;


/**
 * Drop target at current location.
 *
 * @author Daan van Yperen
 */
@All(ActionDrop.class)
public class DropActionSystem extends FluidIteratingSystem {

    private RendererSingleton rendererSingleton;

    @Override
    protected void process(E actor) {
        ActionDrop action = actor.getActionDrop();
        if (action.inventory != -1 && action.target != -1 && actor.hasHolding() && !actor.isMoving()) {
            putItemInside(actor, E.E(action.target), E.E(action.inventory));
        }
        actor.removeActionDrop();
    }

    private void putItemInside(E actor, E itemE, E inventoryE) {
        final Inventory inventory = inventoryE.getInventory();
        final E mergeStack = InventoryUtils.getFirstStackOf(inventory, itemE.itemType());
        if (mergeStack != null) {
            System.out.println("Merge " + itemE + " with stack " + mergeStack);
            mergeStack.getItem().count++;
            itemE.deleteFromWorld();
            stopHolding(actor);
        } else {
            if (!inventory.isFull()) {
                System.out.println("Put  " + itemE + " into " + inventoryE);

                // Remove item from source inventory.
                InventoryUtils.removeFromInventory(itemE);

                inventory.contents.add(itemE.id());
                itemE.insideId(inventoryE.id());
                stopHolding(actor);

                if ( inventory.mode == Inventory.Mode.HOPPER ) {
                    itemE.floating();
                }

                // @todo separate this out to something else.
                itemE
                        .scale(1f)
                        .tint(Tint.WHITE)
                        .gridPos(inventoryE.getGridPos())
                        .renderLayer(GameRules.LAYER_ITEM);

                rendererSingleton.sortedDirty = true;
            }
        }
    }

    private void stopHolding(E actor) {
        actor.removeHolding();
        if (actor.hasPlayer())
            E.E().playSound("sfx_putdown");
    }

}
