package net.mostlyoriginal.game.system.mechanics;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.utils.IntBag;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.util.Cooldown;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.component.dialog.DialogSingleton;
import net.mostlyoriginal.game.events.EventItemPickup;
import net.mostlyoriginal.game.system.control.Days;
import net.mostlyoriginal.game.system.control.NameHelper;

import static net.mostlyoriginal.api.utils.Duration.seconds;

/**
 * @author Daan van Yperen
 */
public class TutorialSystem extends BaseSystem {

    private DialogSingleton dialog;
    private Player player;

    public Cooldown cooldown = Cooldown.withInterval(seconds(3)).autoReset(false);
    public int stage = 0;
    private E playerE;
    private E altarE;
    private IntBag contents;

    @Override
    protected boolean checkProcessing() {
        playerE = E.withTag("player");
        altarE = E.withTag("altar");
        player = playerE != null ? playerE.getPlayer() : null;
        return player != null && !playerE.isInDialog();
    }

    int previousDay = -1;

    @Override
    protected void processSystem() {
        if (previousDay != this.player.day) {
            previousDay = this.player.day;
            stage = 0;
            cooldown.restart();
        }

        if (cooldown.ready(world.delta)) {
            if (this.player.day == Days.FIRST_DAY_IN_THE_SHOP && this.player.nighttime) {
                firstDayInTheShopTutorials();
            }
            if (this.player.day == Days.ENCHANTED_BOW_BUYER && this.player.nighttime) {
                bowBuyerTutorials();
            }
            if (this.player.day == Days.DRUID_PACKAGE && this.player.nighttime) {
                druidTutorials();
            }
            if (this.player.day == Days.CURIOUS_IMP && this.player.nighttime) {
                curiousImpTutorials();
            }
        }
    }

    private void druidTutorials() {
        switch (stage) {
            case 0:
                startTutorialSecondNight();
                break;
        }
    }

    private void curiousImpTutorials() {
        switch (stage) {
            case 0:
                startTutorialThirdNight();
                break;
        }
    }


    private void firstDayInTheShopTutorials() {
        switch (stage) {
            case 0:
                firstDayPhase0();
                break;
        }
    }

    private void firstDayPhase0() {
        stage++;
    }

    private void bowBuyerTutorials() {
        switch (stage) {
            case 0:
                startTutorial();
                break;
            case 1:
                twigInstructions();
                break;
            case 2:
                herbInstructions();
                break;
            case 3:
                storageInstructions();
                break;
            case 4:
                chickOnAltarInstructions();
                break;
            case 5:
                selfOnAltarInstructions();
                break;
            case 6:
                deagedSuccess();
                break;
            case 7:
                deagedSuccess2();
                break;
            case 8:
                doorInstructions();
                break;
        }
    }

    private void doorInstructions() {
        if (isHolding("item_enchanted_bow")) {
            dialog.add(NameHelper.getActor_player_face(), "There we go!");
            dialog.add(NameHelper.getActor_player_face(), "Time to open the shop.");
            dialog.add(NameHelper.getActor_player_face(), "Click the door when ready.");
            stage++;
        }
    }

    private void chickOnAltarInstructions() {
        if (isHolding(null)) {
            dialog.add(NameHelper.getActor_player_face(), "A chick on the altar should do the trick.");
            stage++;
        }
    }

    private void selfOnAltarInstructions() {
        if (isSlotted("item_chick")) {
            dialog.add(NameHelper.getActor_player_face(), "Now to step on an empty altar spot...");
            stage++;
        }
    }

    private void deagedSuccess() {
        if (player.age <= 2) {
            dialog.add(NameHelper.getActor_player_face(), "Ah to be young again.");
            dialog.add(NameHelper.getActor_player_face(), "I need to be younger though.");
            dialog.add(NameHelper.getActor_player_face(), "Twice more.");
            stage++;
        }
    }

    private void deagedSuccess2() {
        if (player.age == 0) {
            dialog.add(NameHelper.getActor_player_face(), "Ah to be young again!");
            dialog.add(NameHelper.getActor_player_face(), "My youth will enchant the bow!");
            dialog.add(NameHelper.getActor_player_face(), "Bow + boy on altar...");
            stage++;
        }
    }


    private void twigInstructions() {
        if (isHolding("item_wood")) {
            dialog.add(NameHelper.getActor_player_face(), "Great!");
            dialog.add(NameHelper.getActor_player_face(), "Now to drop it on the altar.");
            stage++;
        }

    }

    private void herbInstructions() {
        if (isSlotted("item_wood")) {
            dialog.add(NameHelper.getActor_player_face(), "Recipes appear below the altar.");
            dialog.add(NameHelper.getActor_player_face(), "Add the missing bow ingredients.");
            stage++;
        }

    }


    private void storageInstructions() {
        if (isHolding("item_bow")) {
            dialog.add(NameHelper.getActor_player_face(), "Bow! Now to imbue it with youth.");
            dialog.add(NameHelper.getActor_player_face(), "Drats.. I'm an old fart.");
            dialog.add(NameHelper.getActor_player_face(), "Time for some necromancy.");
            dialog.add(NameHelper.getActor_player_face(), "Eh eh eh.");
            dialog.add(NameHelper.getActor_player_face(), "Lets put the bow away for now");
            stage++;
        }

    }

    private boolean isSlotted(String itemType) {
        contents = altarE.getMachine().contents;
        for (int i = 0, s = contents.size(); i < s; i++) {
            if (E.E(contents.get(i)).itemType().equals(itemType)) return true;
        }
        return false;
    }

    private boolean isHolding(String itemType) {
        if (itemType == null) return !playerE.hasHolding();
        return playerE.hasHolding() && E.E(playerE.holdingId()).itemType().equals(itemType);
    }

    private void startTutorial() {
        dialog.add(NameHelper.getActor_player_face(), "An enchanted bow eh? I'll make it!");
        dialog.add(NameHelper.getActor_player_face(), "First I need to grab a twig!");
        stage++;
    }

    private void startTutorialSecondNight() {
        dialog.add(NameHelper.getActor_player_face(), "Herbs are great for potions.");
        dialog.add(NameHelper.getActor_player_face(), "I should put these away.");
        dialog.add(NameHelper.getActor_player_face(), "Perhaps craft some things to restock.");
        dialog.add(NameHelper.getActor_player_face(), "Click the door when ready.");
        stage++;
    }


    private void startTutorialThirdNight() {
        dialog.add(NameHelper.getActor_player_face(), "Imp skin makes great armor.");
        dialog.add(NameHelper.getActor_player_face(), "Fairly easy to breed as well!");
        dialog.add(NameHelper.getActor_player_face(), "I should save my chicks.");
        dialog.add(NameHelper.getActor_player_face(), "Click the door when ready.");
        stage++;
    }

    @Subscribe
    public void onItemPickupTriggerTutorial(EventItemPickup event) {
        String itemType = E.E(event.id).itemType();
        if ("item_boxed_forge".equals(itemType) ||
                "item_boxed_bush".equals(itemType) ||
                "item_boxed_coop".equals(itemType)) {
            dialog.add(NameHelper.getActor_player_face(), "A magical tome!");
            dialog.add(NameHelper.getActor_player_face(), "This will passively create items for me.");
            dialog.add(NameHelper.getActor_player_face(), "I just need some space.");
            dialog.add(NameHelper.getActor_player_face(), "Lots of space in the left of my shop!");
            dialog.add(NameHelper.getActor_player_face(), "LEts drop it on one of these spots..");
        }
    }
}
