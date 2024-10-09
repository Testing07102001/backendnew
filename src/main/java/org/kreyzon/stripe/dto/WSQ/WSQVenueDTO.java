package org.kreyzon.stripe.dto.WSQ;

public class WSQVenueDTO {
    private Long id;
    private int roomno;

    // Constructors, Getters, and Setters
    public WSQVenueDTO(Long id,  int roomno) {
        this.id = id;
        this.roomno = roomno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRoomno() {
        return roomno;
    }

    public void setRoomno(int roomno) {
        this.roomno = roomno;
    }
}
