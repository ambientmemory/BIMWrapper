using System;
using System.Collections.Generic;
using System.Text;
using Xbim.XbimExtensions;
using Xbim.Ifc.SharedBldgElements;
using Xbim.XbimExtensions.Transactions;
using Xbim.Ifc.Kernel;
using Xbim.Ifc.Extensions;
using Xbim.Ifc.ProfileResource;
using Xbim.Ifc.GeometryResource;
using Xbim.Ifc.GeometricModelResource;
using Xbim.Ifc.RepresentationResource;
using Xbim.Ifc.GeometricConstraintResource;
using System.Runtime.CompilerServices;
using Xbim.IO;
using System.IO;
using Xbim.Ifc.ProductExtension; // we need this to use extension methods in VS 2005


namespace WrapperBIM
{
    public class WrapperClasses
    {
        public static IfcBuilding WrapperCreateBuilding(IModel model, string name, double elevHeight)
        {
            using (Transaction txn = model.BeginTransaction("Create Building"))
            {
                IfcBuilding building = model.New<IfcBuilding>();
                building.Name = name;
                building.ElevationOfRefHeight = elevHeight;
                building.CompositionType = IfcElementCompositionEnum.ELEMENT;
                model.IfcProject.AddBuilding(building);
                //validate and commit changes
                if (model.Validate(Console.Out) == 0)
                {
                    txn.Commit();
                    return building;
                }
                else txn.Rollback();
            }
            return null;
        }//end of WrapperCreateBuilding

        public static int WrapperRead(String ifcFileName, IModel model)
        {
            IfcInputStream input = new IfcInputStream(new FileStream(ifcFileName, FileMode.Open, FileAccess.Read)); 
            //get a stream to access the ifc data 

            int errors = input.Load(model); 
            //load entities into the model

            Console.WriteLine("Loaded file name:" + ifcFileName+"; Errors found: " + errors+". Returning now.");
            input.Close(); //closing filestream
            model.Close();
            return errors;
        }//end of WrapperRead

        public static void WrapperWrite(String ifcFileName, IModel model)
        {
            Console.WriteLine("Writing to: " + ifcFileName);
            IfcOutputStream ifcOut = new IfcOutputStream(new StreamWriter(ifcFileName)); //create a stream to output the ifc data to
            ifcOut.Store(model);
            Console.WriteLine("SUCESS. #MUH #MEETHA #KARO #BHAI. Model ab close hone waali hai.");
            model.Close();
            Console.WriteLine("Press any key to exit...BAI");
            Console.ReadKey();
        }//end of WrapperWrite

        public static int WrapperValidateModel(IModel modelName)
        {       //Start with the validation 
                int valErrors = modelName.Validate(Console.Out, null, ValidationFlags.All);
               return valErrors;
        }//end of WrapperValidateModel

        public static IModel WrapperCreateandInitModel(string projectName, string FamilyName, string OrgName, string AppIdentifier, string DevName, string AppFullName, string version)
        {
            IModel model = new Xbim.XbimExtensions.XbimMemoryModel(); //create an empty model

            //Begin a transaction as all changes to a model are transacted
            using (Transaction txn = model.BeginTransaction("Initialise Model"))
            {
                //do once only initialisation of model application and editor values
                model.DefaultOwningUser.ThePerson.FamilyName = FamilyName;
                model.DefaultOwningUser.TheOrganization.Name = OrgName;
                model.DefaultOwningApplication.ApplicationIdentifier = AppIdentifier;
                model.DefaultOwningApplication.ApplicationDeveloper.Name = DevName;
                model.DefaultOwningApplication.ApplicationFullName = AppFullName;
                model.DefaultOwningApplication.Version = version;

                //set up a project and initialise the defaults

                IfcProject project = model.New<IfcProject>();
                project.Initialize(ProjectUnits.SIUnitsUK);
                project.Name = projectName;

                if (WrapperValidateModel(model) == 0)
                {
                    txn.Commit();
                    return model;
                }
                else txn.Rollback();
            }
            return null; //failed so return nothing
        }//end of CreateandInitModel

        public static IfcWallStandardCase WrapperCreateWall(IModel model, double length, double width, double height, string wallName, int cartX, int cartY, int extrudeX, int extrudeY, int extrudeZ, string RepType, string RepID, int refdirX, int refdirY, int refdirZ, int axisSetX, int axisSetY, int axisSetZ)
        {
            //begin a transaction
            using (Transaction txn = model.BeginTransaction("Create Wall"))
            {
                IfcWallStandardCase wall = model.New<IfcWallStandardCase>();
                wall.Name = wallName;

                //represent wall as a rectangular profile
                IfcRectangleProfileDef rectProf = model.New<IfcRectangleProfileDef>();
                rectProf.ProfileType = IfcProfileTypeEnum.AREA;
                rectProf.XDim = width;
                rectProf.YDim = length;

                IfcCartesianPoint insertPoint = model.New<IfcCartesianPoint>();
                insertPoint.SetXY(cartX, cartY); //insert at arbitrary position
                rectProf.Position = model.New<IfcAxis2Placement2D>();
                rectProf.Position.Location = insertPoint;

                //model as a swept area solid
                IfcExtrudedAreaSolid body = model.New<IfcExtrudedAreaSolid>();
                body.Depth = height;
                body.SweptArea = rectProf;
                body.ExtrudedDirection = model.New<IfcDirection>();
                body.ExtrudedDirection.SetXYZ(extrudeX, extrudeY, extrudeZ);

                //parameters to insert the geometry in the model
                IfcCartesianPoint origin = model.New<IfcCartesianPoint>();
                origin.SetXYZ(0, 0, 0);
                body.Position = model.New<IfcAxis2Placement3D>();
                body.Position.Location = origin;

                //Create a Definition shape to hold the geometry
                IfcShapeRepresentation shape = model.New<IfcShapeRepresentation>();
                shape.ContextOfItems = model.IfcProject.ModelContext();
                shape.RepresentationType = RepType;
                shape.RepresentationIdentifier = RepID;
                shape.Items.Add_Reversible(body);

                //Create a Product Definition and add the model geometry to the wall
                IfcProductDefinitionShape rep = model.New<IfcProductDefinitionShape>();
                rep.Representations.Add_Reversible(shape);

                wall.Representation = rep;

                //now place the wall into the model
                IfcLocalPlacement lp = model.New<IfcLocalPlacement>();
                IfcAxis2Placement3D ax3d = model.New<IfcAxis2Placement3D>();
                ax3d.Location = origin;
                ax3d.RefDirection = model.New<IfcDirection>();
                ax3d.RefDirection.SetXYZ(refdirX, refdirY, refdirZ);
                ax3d.Axis = model.New<IfcDirection>();
                ax3d.Axis.SetXYZ(axisSetX, axisSetY, axisSetZ);


                lp.RelativePlacement = ax3d;
                wall.ObjectPlacement = lp;

                //validate write any errors to the console and commit if ok, otherwise abort
                if (model.Validate(Console.Out) == 0)
                {
                    txn.Commit();
                    return wall;
                }
                else
                    txn.Rollback();
            }
            return null;
        }

        static void Main(string[] args)
        {
            //first create and initialise a model called Hello Wall
            Console.WriteLine("Initialising the IFC Project....");
            IModel model = WrapperCreateandInitModel("Hello Wall", "Mukherjee", "No Good", "Peels", "Meep", "BooYa", "1.1.1");
            if (model != null)
            {
                IfcBuilding building = WrapperCreateBuilding(model, "Default Building", 2000);
                IfcWallStandardCase wall = WrapperCreateWall(model, 4000, 300, 2400, "Wall-E", 0, 400, 0, 0, 1, "SweptSolid","Body", 0, 1, 0, 0, 0, 1 );
                building.AddElement(wall);
                if (wall != null)
                {
                    try
                    {
                        Console.WriteLine("Standard Wall successfully created....");
                        WrapperWrite("HelloWall.ifc", model); //write the Ifc File
                        Console.WriteLine("HelloWall.ifc has been successfully written");
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Failed to save HelloWall.ifc");
                        Console.WriteLine(e.Message);
                    }
                }
            }
            else Console.WriteLine("Failed to initialise the model");
 
        }

    }//end of class WrapperClasses
}