/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.messagebus.definition;

import ch.quantasy.messagebus.message.definition.Event;
import ch.quantasy.messagebus.message.definition.Intent;
import net.engio.mbassy.bus.MBassador;

/**
 *
 * @author Reto E. Koenig <reto.koenig@bfh.ch>
 */
public interface BusFactory {

    public MBassador<Intent> getIntentBus();

    public MBassador<Event> getEventBus();
}
