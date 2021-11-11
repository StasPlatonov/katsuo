local platformerClass = require "Scripts.PlatformerObject"
local platformerObjectType = luajava.bindClass("bmg.katsuo.gameplay.objects.PlatformerObject")
local playerObjectType = luajava.bindClass("bmg.katsuo.gameplay.objects.PlayerObject")
local App
	
local BOMB_DISNANCE_FROM_PLAYER = 16
local BOMB_SIZE = 16
------------------------------------------------------------------------------------------------------

PlayerObject = {}
------------------------------------------------------------------------------------------------------

function PlayerObject.init(app)
    platformerClass.init(app)
    App = platformerClass.GetApp()
	App:Log("PlayerObject.lua", "PlayerObject.init()")
end
------------------------------------------------------------------------------------------------------

function PlayerObject.kill(obj)
	App:PlaySound("player_die", false)
end
------------------------------------------------------------------------------------------------------

function PlayerObject.kick(obj, contact)
	obj:Kick(contact, "kick")
end
------------------------------------------------------------------------------------------------------

function PlayerObject.damage(obj, damage)
	platformerClass.damage(obj, damage)
end
------------------------------------------------------------------------------------------------------

local function GetBeamX(obj)
	if obj:GetDirection() == platformerObjectType.MoveDirection.MD_RIGHT then
		return obj:GetTrueX() + obj:getWidth()
	else
		return obj:GetTrueX()
    end
end
------------------------------------------------------------------------------------------------------

local function GetBeamAngle(obj)
	if obj:GetDirection() == platformerObjectType.MoveDirection.MD_RIGHT then
		return 0.0
	else
		return 180.0
	end
end
------------------------------------------------------------------------------------------------------

local function GetThrowDirectionX(obj)
	if obj:GetDirection() == platformerObjectType.MoveDirection.MD_RIGHT then
		return 1.0
	else
		return -1.0
	end
end
------------------------------------------------------------------------------------------------------

local function GetBombX(obj)
	if obj:GetDirection() == platformerObjectType.MoveDirection.MD_RIGHT then
		return obj:GetTrueX() + obj:getWidth() + BOMB_DISNANCE_FROM_PLAYER
	else 
		return obj:GetTrueX() - (BOMB_DISNANCE_FROM_PLAYER + BOMB_SIZE)
	end
end
------------------------------------------------------------------------------------------------------

local updateActions = {}

updateActions[platformerObjectType.ES_DYING] = function (obj)
    if obj:GetStateTime() > 2000 then
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

function PlayerObject.update(obj, delta)
	
    platformerClass.update(obj, delta)
    
    if (obj:IsKilled() == true) then
        return
    end

    if obj:IsDying() then
        updateActions[obj:GetCurrentState()](obj)
        return
    end
    
	obj:SetFlip(obj:GetDirection() == platformerObjectType.MoveDirection.MD_LEFT, false)

    local weaponX = GetBeamX(obj)
    local weaponY = obj:GetTrueY() + obj:getHeight() / 2
    local weaponAngle = GetBeamAngle(obj)
    
    obj:SetWeaponParameters(weaponX, weaponY, weaponAngle)
end
------------------------------------------------------------------------------------------------------

function PlayerObject.Fire(obj)
    local objCenterX = obj:GetTrueX() + obj:getWidth() / 2
    local objCenterY = obj:GetTrueY() + obj:getHeight() / 2

	if obj:GetWeaponType() == playerObjectType.WeaponType.WEAPON_BULLET then
		App:PlaySound("fire", false)

        local bulletSpeed = 4.0
        local bvx = bulletSpeed * GetThrowDirectionX(obj)
        local bvy = 0.0
		
		obj:FireBullet(objCenterX, objCenterY, bvx, bvy)

	elseif obj:GetWeaponType() == playerObjectType.WeaponType.WEAPON_BEAM then
	    App:PlaySound("beam", false);

		local angle = GetBeamAngle(obj)
		obj:FireBeam(angle)
		
	elseif obj:GetWeaponType() == playerObjectType.WeaponType.WEAPON_GRENADE then
		App:PlaySound("throw", false)

        local grenadeSpeed = 1.0
        local bvx = grenadeSpeed * GetThrowDirectionX(obj)
        local bvy = grenadeSpeed * 1.0
		
		obj:FireGrenade(objCenterX, objCenterY, bvx, bvy)

	elseif obj:GetWeaponType() == playerObjectType.WeaponType.WEAPON_BOMB then
		App:PlaySound("throw", false)

        local bx = GetBombX(obj)
        local by = obj:GetTrueY()
		
		obj:FireBomb(bx, by)

	end
end
------------------------------------------------------------------------------------------------------
