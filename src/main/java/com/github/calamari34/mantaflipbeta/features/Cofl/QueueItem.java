package com.github.calamari34.mantaflipbeta.features.Cofl;

import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import lombok.Getter;


import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;


// TODO: Implement Lombok Library for clean code
public class QueueItem {
    public @Getter
    final String command, name;
    public @Getter
    final int price;
    public @Getter
    final int target;
    public @Getter
    final String uid;

    public QueueItem(String command, String name, int price, int target, String uid) {
        this.command = command;
        this.name = name;
        this.price = price;
        this.target = target;
        this.uid = uid;
    }

    public void openAuction() {

        Utils.sendServerMessage("/viewauction " + command);



    }
}