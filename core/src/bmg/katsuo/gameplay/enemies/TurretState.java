package bmg.katsuo.gameplay.enemies;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum TurretState implements State<TurretObject>
{
    SHOOT() {
        @Override
        public void enter (TurretObject turret) {
            turret.OnStartShoot();
        }

        @Override
        public void exit (TurretObject turret) {
            turret.OnStopShoot();
        }

        @Override
        public void update(TurretObject turret) {
            if (!turret.IsPlayerDetected())
            {
                turret.FSM.changeState(SLEEP);
            }
            else
            {
                turret.DoShoot();
            }
        }
    },

    SLEEP() {
        @Override
        public void enter (TurretObject turret) {
            turret.OnStartSleep();
        }

        @Override
        public void update(TurretObject turret) {
            if (turret.IsPlayerDetected())
            {
                turret.FSM.changeState(SHOOT);
            }
            else
            {
                turret.DoSleep();
            }
        }
    },

    SLEEP_ARMED() {
        @Override
        public void enter (TurretObject turret) {
            turret.OnStartSleepArmed();
        }

        @Override
        public void update(TurretObject turret) {
            if (turret.IsPlayerDetected())
            {
                turret.FSM.changeState(SHOOT);
            }
            else
            {
                turret.DoSleepArmed();
            }
        }
    };

    @Override
    public void enter(TurretObject turret) {
    }

    @Override
    public void exit(TurretObject turret) {
    }

    @Override
    public boolean onMessage(TurretObject turret, Telegram telegram)
    {
        // We don't use messaging in this example
        return false;
    }
}