/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2021 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.retrooper.packetevents.wrapper.login.server;

import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.UUID;

/**
 * This packet switches the connection state to {@link ConnectionState#PLAY}.
 */
public class WrapperLoginServerLoginSuccess extends PacketWrapper<WrapperLoginServerLoginSuccess> {
    private UUID uuid;
    private String username;

    public WrapperLoginServerLoginSuccess(PacketSendEvent event) {
        super(event);
    }

    public WrapperLoginServerLoginSuccess(UUID uuid, String username) {
        super(PacketType.Login.Server.LOGIN_SUCCESS);
        this.uuid = uuid;
        this.username = username;
    }

    public static int[] serializeUUID(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        return new int[]{(int) (mostSigBits >> 32), (int) mostSigBits, (int) (leastSigBits >> 32), (int) leastSigBits};
    }

    @Override
    public void readData() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16)) {
            int[] data = new int[4];
            for (int i = 0; i < 4; i++) {
                data[i] = readInt();
            }
            this.uuid = deserializeUUID(data);
        } else {
            this.uuid = UUID.fromString(readString(36));
        }
        this.username = readString(16);
    }

    @Override
    public void readData(WrapperLoginServerLoginSuccess wrapper) {
        this.uuid = wrapper.uuid;
        this.username = wrapper.username;
    }

    @Override
    public void writeData() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16)) {
            int[] data = serializeUUID(uuid);
            for (int i = 0; i < 4; i++) {
                writeInt(data[i]);
            }
        } else {
            writeString(uuid.toString(), 36);
        }
        writeString(username, 16);
    }

    private UUID deserializeUUID(int[] data) {
        return new UUID((long) data[0] << 32 | data[1] & 4294967295L, (long) data[2] << 32 | data[3] & 4294967295L);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}