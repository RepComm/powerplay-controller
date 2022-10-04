# powerplay-controller

Source code for a REV-31-1152 controller

Base project copied from [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController)

The [TeamCode](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode) directory contains the custom source code for this repo

### User Documentation and Tutorials
- [FtcRobotController Online Documentation](https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki)
- [FTC Javadoc Documentation](https://javadoc.io/doc/org.firstinspires.ftc)
- [FTC Technology Forum](https://ftcforum.firstinspires.org/forum/ftc-technology)

### Implemented
- [Omnidrive](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Omnidrive.java) - math to calculate driving in a direction given arbitrary wheel count and angles
- [Debounce](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Debounce.java) - think windows 'sticky keys' feature
- [Vec2](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Vec2.java) - 2d vector math library (ported from npm @repcomm/vec2 typescript module)
- [Main](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Main.java) - Main OpMode
- [MathEx](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode/MathEx.java) - Extra math functions

### Controls
- `A` : toggle align mode vs raw turn mode
- `Right/Left bumper` : turn 90 degrees (only in align mode)
- `Left stick` : drive direction / magnitude
- `Right stick X` : turn / align turn
