package study.springbook.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) {
                member.setLevel(level);
                member.upgradeLevel();
                assertEquals(member.getLevel(), level.nextLevel());
            }
        }
    }

    @Test
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) {
                continue;
            }
            member.setLevel(level);
            assertThrows(IllegalStateException.class,() -> member.upgradeLevel());
        }
    }
}