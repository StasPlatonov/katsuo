local platformerObjectType = luajava.bindClass("bmg.katsuo.gameplay.objects.PlatformerObject")
------------------------------------------------------------------------------------------------------

PlatformerObject = {}
------------------------------------------------------------------------------------------------------

local App

function PlatformerObject.init(app)
	App = app
	App:Log("PlatformerObject.lua", "PlatformerObject.init()")
end
------------------------------------------------------------------------------------------------------

function PlatformerObject.GetApp()
    return App
end
------------------------------------------------------------------------------------------------------

function PlatformerObject.damage(obj, damage)
	obj:SetAccumulatedDamage(obj:GetAccumulatedDamage() + damage)

	local intDmgPart = math.floor(obj:GetAccumulatedDamage())

    if obj:GetAccumulatedDamage() >= 1 then
        obj:SetHealth(obj:GetHealth() - intDmgPart);

        obj:SetAccumulatedDamage(obj:GetAccumulatedDamage() - intDmgPart)
    end
end
------------------------------------------------------------------------------------------------------

function PlatformerObject.update(obj, delta)
    if obj:GetHealth() <= 0 then
		obj:InitKill()
	end

    if obj:GetCurrentState() == platformerObjectType.ES_DYING then
        obj:SetState(platformerObjectType.ES_KILLED)
    end
    
	if obj:IsKilled() == true then
		obj:remove()
    else
        obj:CheckGround()
    end
end
------------------------------------------------------------------------------------------------------

return PlatformerObject;