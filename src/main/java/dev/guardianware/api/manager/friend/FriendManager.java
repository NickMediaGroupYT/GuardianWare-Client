package dev.guardianware.api.manager.friend;

import java.util.ArrayList;

public class FriendManager {
    private final ArrayList<Friend> friends = new ArrayList<>();

    public ArrayList<Friend> getFriends() {
        return this.friends;
    }

    public Friend getFriend(String name) {
        return this.friends.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean isFriend(String name) {
        for (Friend friend : this.getFriends()) {
            if (!friend.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    public void addFriend(String name) {
        this.friends.add(new Friend(name));
    }

    public void removeFriend(String name) {
        if (this.getFriend(name) != null) {
            this.friends.remove(this.getFriend(name));
        }
    }

    public void clearFriends() {
        this.friends.clear();
    }
}
