local platformerClass = require "Scripts.PlatformerObject"
local platformerObjectType = luajava.bindClass("bmg.katsuo.gameplay.objects.PlatformerObject")
local App
	
------------------------------------------------------------------------------------------------------

WalkingEnemyObject = {}
------------------------------------------------------------------------------------------------------

function WalkingEnemyObject.init(app)
	platformerClass.init(app)
	App = platformerClass.GetApp()
	App:Log("WalkingEnemyObject.lua", "WalkingEnemyObject.init()")
end
------------------------------------------------------------------------------------------------------

function WalkingEnemyObject.kill(obj)
	App:PlaySound("enemy_die", false)
end
------------------------------------------------------------------------------------------------------

function WalkingEnemyObject.damage(obj, damage)
	platformerClass.damage(obj, damage)
end
------------------------------------------------------------------------------------------------------

function WalkingEnemyObject.kick(obj, force, currentTime)
	if (currentTime - obj:GetLastKickTime()) > 1000 then
		obj:Kick(force, "kick")
		return true
	end
	return false
end
------------------------------------------------------------------------------------------------------

local updateActions = {}
        
updateActions[platformerObjectType.ES_DYING] = function (obj)
    if obj:GetStateTime() > 1000 then
        obj:SetState(platformerObjectType.ES_DEAD)
    end
end

updateActions[platformerObjectType.ES_DEAD] = function (obj) 
    if obj:GetStateTime() > 1000 then
        obj:SetKilled()
    else
        local k = obj:GetStateTime() / 1000.0
        local clr = obj:getColor()
        clr.a = 1.0 - k
        obj:setColor(clr)
        obj:SetSpeed(0.0, 1.5 * k)
    end
end

updateActions[platformerObjectType.ES_KILLED] = function (obj) end
------------------------------------------------------------------------------------------------------

function WalkingEnemyObject.update(obj, delta)
    platformerClass.update(obj, delta)
    
    if (obj:IsKilled() == true) then
        return;
    end

    if obj:IsDying() then
        updateActions[obj:GetCurrentState()](obj)
        return
    end
    
    obj:SetFlip(obj:GetDirection() == platformerObjectType.MoveDirection.MD_RIGHT, false) 
end
------------------------------------------------------------------------------------------------------
