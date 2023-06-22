package shareit.booking;

import java.util.Arrays;

public enum BookingState {

    WAITING,
    REJECTED,
    CURRENT,
    FUTURE,
    PAST,
    ALL;

    public static String checkState(String state) {
        return Arrays.stream(BookingState.values())
                .map(BookingState::name)
                .filter(x -> x.equals(state))
                .findFirst()
                .orElse("");
    }
}
