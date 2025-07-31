package com.freefish.torchesbecomesunlight.server.partner.command;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PartnerCommandManager {
    @Getter
    private final List<PartnerCommandBasic> partnerCommandBasics = new ArrayList<>();
    @Getter@Setter
    private PartnerCommandBasic currentCommand;

    public void addCommand(PartnerCommandBasic partnerCommandBasic){
        partnerCommandBasics.add(partnerCommandBasic);
    }
}
